package org.ak2.gui.actions;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.ak2.utils.LengthUtils;

public class ActionDispatcher {

    /**
     * Name for additional action parameters.
     */
    public static final String PARAMETERS = "Parameters";

    private static Map<Class<?>, Map<String, Method>> s_methods = new HashMap<Class<?>, Map<String, Method>>();

    public static boolean dispatch(final ActionEx action) {
        final ActionEvent originalEvent = action.getOriginalEvent();
        if (originalEvent != null) {
            final Object source = originalEvent.getSource();
            if (source instanceof JComponent) {
                JComponent comp = (JComponent) source;
                do {
                    final Object controller = ActionController.getController(comp);
                    if (controller != null) {
                        if (dispatch(controller, action)) {
                            return true;
                        }
                    }
                    if (dispatch(comp, action)) {
                        return true;
                    }
                    final Container parent = comp.getParent();
                    comp = (JComponent) (parent instanceof JComponent ? parent : null);
                } while (comp != null);
            } else {
                return dispatch(source, action);
            }
        }
        return false;
    }

    public static boolean dispatch(final Object controller, final String actionId, final Object... parameters) {
        final ActionEx action = new ActionEx(actionId);
        action.putValue(PARAMETERS, parameters);
        return dispatch(controller, action);
    }

    public static boolean dispatch(final Object controller, final ActionEx action) {
        final Method method = getMethod(controller, action.getId());
        if (method == null) {
            return false;
        }

        final InvocationContext an = method.getAnnotation(InvocationContext.class);
        final InvocationType type = an != null ? an.name() : InvocationType.Direct;

        switch (type) {
        case AsyncSwing:
            if (EventQueue.isDispatchThread()) {
                directInvoke(controller, method, action);
            } else {
                try {
                    SwingUtilities.invokeLater(new Task(controller, method, action));
                } catch (final Throwable th) {
                    th.printStackTrace();
                }
            }
            break;
        case SyncSwing:
            if (EventQueue.isDispatchThread()) {
                directInvoke(controller, method, action);
            } else {
                try {
                    SwingUtilities.invokeAndWait(new Task(controller, method, action));
                } catch (final Throwable th) {
                    th.printStackTrace();
                }
            }
            break;
        case SeparatedThread:
            new Thread(new Task(controller, method, action)).start();
            break;
        case Direct:
        default:
            directInvoke(controller, method, action);
            break;
        }

        return true;
    }

    /**
     * Gets the method.
     *
     * @param target
     *            a possible action target
     * @param actionId
     *            the action id
     *
     * @return the method
     */
    private static synchronized Method getMethod(final Object target, final String actionId) {
        final Class<? extends Object> clazz = target.getClass();

        Map<String, Method> methods = s_methods.get(clazz);
        if (methods == null) {
            methods = getActionMethods(clazz);
            s_methods.put(clazz, methods);
        }
        return methods.get(actionId);
    }

    /**
     * Gets the method.
     *
     * @param clazz
     *            an action target class
     *
     * @return the map of action methods method
     */
    private static Map<String, Method> getActionMethods(final Class<?> clazz) {
        final HashMap<String, Method> result = new HashMap<String, Method>();

        final Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                final Class<?>[] args = method.getParameterTypes();
                if (LengthUtils.length(args) == 1 && ActionEx.class.equals(args[0])) {
                    final ActionMethod annotation = method.getAnnotation(ActionMethod.class);
                    if (annotation != null) {
                        for (final String id : annotation.ids()) {
                            result.put(id, method);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Direct invoke of the action.
     *
     * @param action
     *            the action to run
     */
    private static void directInvoke(final Object target, final Method method, final ActionEx action) {
        try {
            method.invoke(target, action);
        } catch (final Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * This class implements thread task for invoked action.
     */
    private static class Task implements Runnable {
        private final Object m_target;
        private final Method m_method;
        private final ActionEx m_action;

        /**
         * Constructor
         *
         * @param action
         *            action to run
         */
        public Task(final Object target, final Method method, final ActionEx action) {
            m_target = target;
            m_method = method;
            m_action = action;
        }

        /**
         *
         * @see java.lang.Runnable#run()
         */
        public synchronized void run() {
            directInvoke(m_target, m_method, m_action);
        }

    }

}
