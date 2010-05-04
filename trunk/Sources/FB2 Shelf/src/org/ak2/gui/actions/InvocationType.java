package org.ak2.gui.actions;

public enum InvocationType {
    /**
     * An action or event executed using the
     * {@link javax.swing.SwingUtilities#invokeLater(Runnable)} method.
     */
    AsyncSwing,
    /**
     * An action or event executed using the
     * {@link javax.swing.SwingUtilities#invokeAndWait(Runnable)} method.
     */
    SyncSwing,
    /**
     * An action or event executed in a separated thread.
     */
    SeparatedThread,
    /**
     * An action or event executed in a current thread.
     */
    Direct;
}