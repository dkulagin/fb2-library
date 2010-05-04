/**
 *
 */
package org.ak2.gui.actions;


public @interface InvocationContext {
    InvocationType name() default InvocationType.Direct;
}
