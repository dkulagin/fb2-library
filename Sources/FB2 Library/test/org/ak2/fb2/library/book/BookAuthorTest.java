package org.ak2.fb2.library.book;

import junit.framework.Assert;

import org.junit.Test;


public class BookAuthorTest {

    @Test
    public void test1() {
        BookAuthor a = new BookAuthor("  last-author fiRst.\n");
        Assert.assertEquals("Last-Author", a.getLastName());
        Assert.assertEquals("First", a.getFirstName());
        Assert.assertEquals("Last-Author First", a.getName());
    }

    @Test
    public void test2() {
        BookAuthor a = new BookAuthor("  fiRst. last-author \n", false);
        Assert.assertEquals("Last-Author", a.getLastName());
        Assert.assertEquals("First", a.getFirstName());
        Assert.assertEquals("Last-Author First", a.getName());
    }
}
