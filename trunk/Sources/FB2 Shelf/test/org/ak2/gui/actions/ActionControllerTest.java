package org.ak2.gui.actions;

import org.junit.Assert;
import org.junit.Test;

public class ActionControllerTest {

    @Test
    public void testFilterFieldActions() {
        ActionController c = new ActionController("FilterField");

        ActionEx clearAction = c.getAction("clear");
        Assert.assertNotNull(clearAction);

        Assert.assertEquals("clear", clearAction.getId());
        Assert.assertEquals("Clear filter text", clearAction.getDescription());
        Assert.assertNotNull(clearAction.getIcon());
    }

    @Test
    public void testTitledTreePanelActions() {
        ActionController c = new ActionController("TitledTreePanel");

        ActionEx expand = c.getAction("expand");
        Assert.assertNotNull(expand);

        Assert.assertEquals("expand", expand.getId());
        Assert.assertEquals("Expand all", expand.getDescription());
        Assert.assertNotNull(expand.getIcon());

        ActionEx collapse = c.getAction("collapse");
        Assert.assertNotNull(collapse);

        Assert.assertEquals("collapse", collapse.getId());
        Assert.assertEquals("Collapse all", collapse.getDescription());
        Assert.assertNotNull(collapse.getIcon());

    }

}
