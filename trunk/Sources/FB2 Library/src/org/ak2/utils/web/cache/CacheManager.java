package org.ak2.utils.web.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.ak2.utils.StreamUtils;
import org.ak2.utils.web.IWebContent;
import org.ak2.utils.web.WebContentType;

public class CacheManager {

    private static final String DEFAULT_FOLDER = "./cache";

    private static CacheManager s_instance;

    private final File m_folder;

    private final File m_catalog;

    private AtomicLong m_seq = new AtomicLong();

    private Map<URL, CachedContent> m_contents;

    private CacheManager() {
        m_folder = new File(DEFAULT_FOLDER);
        m_folder.mkdirs();
        m_catalog = new File(m_folder, "cache.bin");
        if (m_catalog.exists()) {
            load();
        } else {
            m_contents = new HashMap<URL, CachedContent>();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                save();
            }
        });
    }

    public IWebContent get(URL url) {
        return m_contents.get(url);
    }

    public IWebContent set(IWebContent content) throws IOException {
        CachedContent cached = new CachedContent(content);
        m_contents.put(cached.getUrl(), cached);
        return cached;
    }

    byte[] loadFromFile(String id) throws IOException {
        File f = new File(m_folder, id);
        if (f.exists()) {
            return StreamUtils.getBytes(new FileInputStream(f));
        }
        return null;
    }

    String saveToFile(byte[] content, WebContentType type) throws IOException {
        File f = getFileToSave(type);
        FileOutputStream out = new FileOutputStream(f);
        try {
            out.write(content);
            out.flush();
            return f.getName();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    File getFileToSave(WebContentType type) {
        String ext = type.getExtension();
        while (true) {
            String fileName = String.format("%016d%s", m_seq.incrementAndGet(), ext);
            File f = new File(m_folder, fileName);
            if (!f.exists()) {
                return f;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(m_catalog));
            m_seq.set(in.readLong());
            m_contents = (Map<URL, CachedContent>) in.readObject();
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    private void save() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(m_catalog));
            out.writeLong(m_seq.get());
            out.writeObject(m_contents);
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public static CacheManager getInstance() {
        if (s_instance == null) {
            s_instance = new CacheManager();
        }
        return s_instance;
    }
}
