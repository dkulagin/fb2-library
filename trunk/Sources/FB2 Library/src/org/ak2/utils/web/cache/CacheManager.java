package org.ak2.utils.web.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.ak2.utils.StreamUtils;
import org.ak2.utils.web.IWebContent;
import org.ak2.utils.web.WebContentType;
import org.json.JSONArray;
import org.json.JSONObject;

public class CacheManager implements Iterable<CachedContent> {

    private static final String DEFAULT_FOLDER = "./cache";

    private static boolean s_checkOnStart = false;

    private static CacheManager s_instance;

    private final File m_folder;

    private final File m_catalog;

    private final AtomicLong m_seq = new AtomicLong();

    private final Map<URL, CachedContent> m_contents = new LinkedHashMap<URL, CachedContent>();

    private CacheManager() {
        m_folder = new File(DEFAULT_FOLDER);
        m_folder.mkdirs();
        m_catalog = new File(m_folder, ".cache");
        if (m_catalog.exists()) {
            load();
        }
    }

    public synchronized IWebContent get(final URL url) {
        final CachedContent content = m_contents.get(url);
        if (content == null) {
            return null;
        }
        if (checkFile(content)) {
            return content;
        }
        m_contents.remove(url);
        save();
        return null;
    }

    public synchronized IWebContent set(final IWebContent content) throws IOException {
        final CachedContent cached = new CachedContent(content);
        m_contents.put(cached.getUrl(), cached);
        save();
        return cached;
    }

    @Override
    public Iterator<CachedContent> iterator() {
        return Collections.unmodifiableCollection(m_contents.values()).iterator();
    }

    byte[] loadFromFile(final String id) throws IOException {
        final File f = new File(m_folder, id);
        if (f.exists()) {
            return StreamUtils.getBytes(new FileInputStream(f));
        }
        return null;
    }

    boolean checkFile(final CachedContent content) {
        if (content == null) {
            return false;
        }
        final File f = new File(m_folder, content.getId());
        return f.exists();
    }

    boolean deleteFile(final String id) {
        final File f = new File(m_folder, id);
        return f.delete();
    }

    String saveToFile(final byte[] content, final WebContentType type) throws IOException {
        final File f = getFileToSave(type);
        final FileOutputStream out = new FileOutputStream(f);
        try {
            out.write(content);
            out.flush();
            return f.getName();
        } finally {
            try {
                out.close();
            } catch (final IOException ex) {
            }
        }
    }

    File getFileToSave(final WebContentType type) {
        final String ext = type.getExtension();
        while (true) {
            final String fileName = String.format("%016d%s", m_seq.incrementAndGet(), ext);
            final File f = new File(m_folder, fileName);
            if (!f.exists()) {
                return f;
            }
        }
    }

    private void load() {
        try {
            final String text = StreamUtils.getText(new FileInputStream(m_catalog));
            final JSONObject json = new JSONObject(text);
            m_seq.set(json.getLong("id"));

            final JSONArray jsonArray = json.getJSONArray("cached");
            final int length = jsonArray.length();
            final Set<String> ids = new HashSet<String>();
            for (int i = 0; i < length; i++) {
                final CachedContent content = new CachedContent(jsonArray.getJSONObject(i));
                if (checkFile(content)) {
                    ids.add(content.getId());
                    m_contents.put(content.getUrl(), content);
                }
            }
            if (s_checkOnStart) {
                deleteUnknownFiles(ids);
            }
            save();
        } catch (final Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    private void deleteUnknownFiles(final Set<String> ids) {
        final File[] files = m_folder.listFiles();
        for (final File file : files) {
            if (!ids.contains(file.getName())) {
                file.delete();
            }
        }
    }

    private void save() {
        try {
            final FileWriter out = new FileWriter(m_catalog);
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", m_seq.get());
                obj.put("cached", m_contents.values());
                out.append(obj.toString(2));
            } finally {
                StreamUtils.close(out);
            }
        } catch (final Exception ex) {
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
