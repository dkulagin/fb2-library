package org.ak2.gui.actions;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.ak2.gui.resources.ResourceManager;
import org.ak2.utils.LengthUtils;
import org.ak2.utils.StreamUtils;
import org.ak2.utils.jlog.JLogMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActionController {

    private static final String ACTION_CONTROLLER = "ACTION_CONTROLLER";
    private final HashMap<String, ActionEx> m_actions = new HashMap<String, ActionEx>();

    public ActionController(String name) {
        init(name);
    }

    public ActionController(Class<? extends JComponent> component) {
        List<String> names = new LinkedList<String>();
        for (Class<?> c = component; c != null; c = c.getSuperclass()) {
            if (c.getPackage().getName().startsWith("javax.swing")) {
                break;
            }
            names.add(0, c.getSimpleName());
        }

        for (String name : names) {
            init(name);
        }
    }

    protected void init(String name) {
        String path = "/ui/actions/" + name + ".json";
        try {
            InputStream resource = ResourceManager.getInstance().getResource(path);
            if (resource == null) {
                new JLogMessage("No resource found: {0}").log(path);
                return;
            }
            StringBuilder text = StreamUtils.loadText(resource, "UTF8");
            JSONObject root = new JSONObject(text.toString());
            JSONArray actions = root.optJSONArray("actions");
            if (actions != null) {
                for (int i = 0, n = actions.length(); i < n; i++) {
                    JSONObject object = actions.getJSONObject(i);
                    ActionEx action = createAction(object);
                    ActionEx oldAction = m_actions.get(action.getId());
                    if (oldAction == null) {
                        m_actions.put(action.getId(), action);
                    } else {
                        update(oldAction, action);
                    }
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public ActionEx getAction(final String id) {
        return m_actions.get(id);
    }

    protected ActionEx createAction(JSONObject object) throws JSONException {
        String id = object.getString("id");
        ActionEx action = new ActionEx(id, this);

        action.setText(object.optString("text", action.getText()));
        action.setDescription(object.optString("desc", action.getDescription()));

        final String iconPath = object.optString("icon", null);
        if (LengthUtils.isNotEmpty(iconPath)) {
            final ImageIcon icon = ResourceManager.getInstance().getIcon(iconPath);
            action.setIcon(icon);
        }

        final String accl = object.optString("accl", null);
        if (LengthUtils.isNotEmpty(accl)) {
            final KeyStroke accelerator = KeyStroke.getKeyStroke(accl);
            if (accelerator != null) {
                action.setAccelerator(accelerator);
            }
        }
        return action;
    }

    protected void update(ActionEx oldAction, ActionEx newAction) {
        oldAction.setText(LengthUtils.safeString(newAction.getText(), oldAction.getText()));
        oldAction.setDescription(LengthUtils.safeString(newAction.getDescription(), oldAction.getDescription()));
        oldAction.setIcon(newAction.getIcon());
        oldAction.setAccelerator(newAction.getAccelerator());
    }

    public static void setController(final JComponent comp, Object controller) {
        comp.putClientProperty(ACTION_CONTROLLER, controller);
    }

    public static Object getController(final JComponent comp) {
        return comp.getClientProperty(ACTION_CONTROLLER);
    }
}
