/**
 *
 */
package org.ak2.gui.models.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.ak2.utils.LengthUtils;
import org.ak2.utils.refs.DirectRef;
import org.ak2.utils.refs.IRef;
import org.ak2.utils.refs.ReferenceUtils;

/**
 * @author Whippet
 *
 */
public abstract class AbstractTreeNode<UserObjectType> implements MutableTreeNode, Cloneable
{
    /**
     * An enumeration that is always empty. This is used when an enumeration
     * of a leaf node's children is requested.
     */
    private static final Enumeration<AbstractTreeNode<?>> EMPTY_ENUMERATION = new EmptyEnumeration();

    /** this node's parent, or null if this node has no parent */
    private AbstractTreeNode<?> m_parent;

    /** array of children, may be null if this node has no children */
    private LinkedList<AbstractTreeNode<?>> m_children = new LinkedList<AbstractTreeNode<?>>();

    /** Tree model */
    private AbstractTreeModel m_model;

    private IRef<UserObjectType> m_object;

    private boolean m_accepted;

    /**
     * Constructor.
     *
     * @param model tree model
     */
    protected AbstractTreeNode(final AbstractTreeModel model)
    {
        this(model, null);
    }

    /**
     * Constructor.
     *
     * @param model tree model
     */
    protected AbstractTreeNode(final AbstractTreeModel model, final UserObjectType userObject)
    {
        m_model = model;
        m_object = new DirectRef<UserObjectType>(userObject);
    }

    protected AbstractTreeNode(final AbstractTreeNode<UserObjectType> original)
    {
        this(original.getModel(), original.getObject());
    }

    /**
     * @return Returns the model.
     */
    public AbstractTreeModel getModel()
    {
        return m_model;
    }

    void setModel(final AbstractTreeModel model)
    {
        m_model = model;
    }

    /**
     * @return the accepted
     */
    public boolean isAccepted()
    {
        return m_accepted;
    }

    /**
     * @param accepted the accepted to set
     */
    void setAccepted(final boolean accepted)
    {
        m_accepted = accepted;
    }

    /**
     * Refresh tree node content
     */
    public void refresh()
    {
        AbstractTreeNode<?> parent = getParentNode();
        if (parent != null)
        {
            getModel().fireNodeChanged(parent, this);
        }
    }

    public void release()
    {
        final List<AbstractTreeNode<?>> children = removeAllChildren();
        for(AbstractTreeNode<?> child : children)
        {
            child.release();
        }
    }

    /**
     * Returns true if the receiver allows children.
     *
     * @return boolean
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren()
    {
        return true;
    }

    /**
     * Returns true if the receiver is a leaf.
     *
     * @return boolean
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf()
    {
        return getChildCount() == 0;
    }

    /**
     * @return
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount()
    {
        return m_children.size();
    }

    /**
     * Returns the child <code>TreeNode</code> at index
     * <code>childIndex</code>.
     *
     * @param index an index into this node's child array
     * @return the {@link TreeNode} in this node's child array at the
     *         specified index
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    public TreeNode getChildAt(final int childIndex)
    {
        return m_children.get(childIndex);
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     *
     * @param aChild the TreeNode to search for among this node's children
     * @return an int giving the index of the node in this node's child
     *         array, or <code>-1</code> if the specified node is a not a
     *         child of this node
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(final TreeNode node)
    {
        if (node == null)
        {
            throw new IllegalArgumentException("argument is null");
        }
        if (!isNodeChild(node))
        {
            return -1;
        }
        return m_children.indexOf(node); // linear search
    }

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     *
     * @return an {@link Enumeration} of this node's children
     * @see javax.swing.tree.TreeNode#children()
     */
    @Deprecated
    public final Enumeration<?> children()
    {
        return getChildren();
    }

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     *
     * @return an {@link Enumeration} of this node's children
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration<AbstractTreeNode<?>> getChildren()
    {
        return Collections.enumeration(m_children);
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     *
     * @return this node's parent {@link TreeNode}, or null if this node
     *         has no parent
     * @see javax.swing.tree.TreeNode#getParent()
     */
    @Deprecated
    public final TreeNode getParent()
    {
        return getParentNode();
    }

    /**
     * Returns the parent <code>AbstractMutableNode</code> of the
     * receiver.
     *
     * @return this node's parent {@link AbstractTreeNode}, or null if
     *         this node has no parent
     */
    @SuppressWarnings("unchecked")
    public <T> AbstractTreeNode<T> getParentNode()
    {
        return (AbstractTreeNode<T>) m_parent;
    }

    /**
     * Sets the parent of the receiver to <code>newParent</code>.
     *
     * @param newParent this node's new parent
     * @see javax.swing.tree.MutableTreeNode#setParent(javax.swing.tree.MutableTreeNode)
     */
    @Deprecated
    public final void setParent(final MutableTreeNode newParent)
    {
        setParentNode((AbstractTreeNode<?>) newParent);
    }

    /**
     * Sets the parent of the receiver to <code>newParent</code>.
     *
     * @param newParent this node's new parent
     */
    public void setParentNode(final AbstractTreeNode<?> newParent)
    {
        try
        {
            if (newParent == null)
            {
                m_object = ReferenceUtils.weak(m_object);
            }
            else
            {
                m_object = ReferenceUtils.direct(m_object);
            }
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
        m_parent = newParent;
    }

    /**
     * Returns this node's user object.
     *
     * @return the Object stored at this node by the user
     */
    public UserObjectType getObject()
    {
        try
        {
            return ReferenceUtils.get(m_object);
        }
        catch (final Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param userObject the Object that constitutes this node's
     *            user-specified data
     * @see javax.swing.tree.MutableTreeNode#setUserObject(java.lang.Object)
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public final void setUserObject(final Object userObject)
    {
        setObject((UserObjectType) userObject);
    }

    /**
     * Sets the user object for this node.
     *
     * @param newObject the Object that constitutes this node's
     *            user-specified data
     */
    public void setObject(final UserObjectType newObject)
    {
        final UserObjectType oldObject = getObject();
        m_object = new DirectRef<UserObjectType>(newObject);
        onUpdate(oldObject, newObject);
    }

    protected void onUpdate(final UserObjectType oldObject, final UserObjectType newObject)
    {
        removeAll();
        refresh();
    }


    /**
     * Removes <code>newChild</code> from its parent and makes it a child
     * of this node by adding it to the end of this node's child array.
     *
     * @see #insert
     * @param newChild node to add as a child of this node
     * @exception IllegalArgumentException if <code>newChild</code> is
     *                null
     * @exception IllegalStateException if this node does not allow children
     */
    public void add(final AbstractTreeNode<?> newChild)
    {
        if (newChild != null && newChild.getParentNode() == this)
        {
            insertChild(newChild, getChildCount() - 1);
        }
        else
        {
            insertChild(newChild, getChildCount());
        }
    }

    /**
     * Adds <code>child</code> to the receiver at <code>index</code>.
     * <code>child</code> will be messaged with <code>setParent</code>.
     *
     * @param child the MutableTreeNode to insert under this node
     * @param index the index in this node's child array where this node is
     *            to be inserted
     * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode,
     *      int)
     */
    @Deprecated
    public final void insert(final MutableTreeNode child, final int index)
    {
        insertChild((AbstractTreeNode<?>) child, index);
    }

    /**
     * Adds <code>child</code> to the receiver at <code>index</code>.
     * <code>child</code> will be messaged with <code>setParent</code>.
     *
     * @param child the {@link AbstractTreeNode} to insert under this
     *            node
     * @param index the index in this node's child array where this node is
     *            to be inserted
     * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode,
     *      int)
     */
    public void insertChild(final AbstractTreeNode<?> child, final int index)
    {
        if (child == null)
        {
            throw new IllegalArgumentException("new child is null");
        }
        else if (isNodeAncestor(child))
        {
            throw new IllegalArgumentException("new child is an ancestor");
        }

        final AbstractTreeNode<?> oldParent = child.getParentNode();

        if (oldParent != null)
        {
            oldParent.removeChild(child);
        }
        child.setParentNode(this);
        m_children.add(index, child);
    }

    /**
     * Removes the child at <code>index</code> from the receiver.
     *
     * @param index the index in this node's child array of the child to
     *            remove
     * @see javax.swing.tree.MutableTreeNode#remove(int)
     */
    public void remove(final int index)
    {
        final AbstractTreeNode<?> child = m_children.remove(index);
        child.setParentNode(null);
    }

    /**
     * Removes <code>node</code> from the receiver. <code>setParent</code>
     * will be messaged on <code>node</code>.
     *
     * @param node a child of this node to remove
     * @see javax.swing.tree.MutableTreeNode#remove(javax.swing.tree.MutableTreeNode)
     */
    @Deprecated
    public final void remove(final MutableTreeNode node)
    {
        removeChild((AbstractTreeNode<?>) node);
    }

    /**
     * Removes <code>node</code> from the receiver. <code>setParent</code>
     * will be messaged on <code>node</code>.
     *
     * @param node a child of this node to remove
     */
    public void removeChild(final AbstractTreeNode<?> node)
    {
        if (node == null)
        {
            throw new IllegalArgumentException("argument is null");
        }

        if (!isNodeChild(node))
        {
            throw new IllegalArgumentException("argument is not a child");
        }
        m_children.remove(node);
        node.setParentNode(null);
    }

    /**
     * Removes the receiver from its parent.
     *
     * @see javax.swing.tree.MutableTreeNode#removeFromParent()
     */
    public void removeFromParent()
    {
        final AbstractTreeNode<?> parent = getParentNode();
        if (parent != null)
        {
            parent.removeChild(this);
        }
    }

    /**
     * Removes all of this node's children, setting their parents to null.
     * If this node has no children, this method does nothing.
     */
    public List<AbstractTreeNode<?>> removeAllChildren()
    {
        final ArrayList<AbstractTreeNode<?>> nodes = new ArrayList<AbstractTreeNode<?>>(m_children);

        for(int i = getChildCount() - 1; i >= 0; i--)
        {
            remove(i);
        }

        return nodes;
    }

    /**
     * Removes all children
     */
    protected void removeAll()
    {
        List<AbstractTreeNode<?>> children = removeAllChildren();
        if (children != null && children.size() > 0)
        {
            final Object[] nodes = children.toArray();
            getModel().fireAllNodesRemoved(this, nodes);
        }
    }


    /**
     * Returns the root of the tree that contains this node. The root is the
     * ancestor with a null parent.
     *
     * @see #isNodeAncestor
     * @return the root of the tree that contains this node
     */
    public AbstractTreeNode<?> getRoot()
    {
        AbstractTreeNode<?> ancestor = this;
        AbstractTreeNode<?> previous;
        do
        {
            previous = ancestor;
            ancestor = ancestor.getParentNode();
        } while (ancestor != null);

        return previous;
    }

    /**
     * Returns true if <code>aNode</code> is a child of this node. If
     * <code>aNode</code> is null, this method returns false.
     *
     * @return true if <code>aNode</code> is a child of this node; false
     *         if <code>aNode</code> is null
     */
    public boolean isNodeChild(final TreeNode aNode)
    {
        boolean retval;

        if (aNode == null)
        {
            retval = false;
        }
        else
        {
            if (getChildCount() == 0)
            {
                retval = false;
            }
            else
            {
                retval = aNode.getParent() == this;
            }
        }

        return retval;
    }

    /**
     * Returns true if <code>anotherNode</code> is an ancestor of this
     * node -- if it is this node, this node's parent, or an ancestor of
     * this node's parent. (Note that a node is considered an ancestor of
     * itself.) If <code>anotherNode</code> is null, this method returns
     * false. This operation is at worst O(h) where h is the distance from
     * the root to this node.
     *
     * @see #isNodeDescendant
     * @see #getSharedAncestor
     * @param anotherNode node to test as an ancestor of this node
     * @return true if this node is a descendant of <code>anotherNode</code>
     */
    public boolean isNodeAncestor(final TreeNode anotherNode)
    {
        if (anotherNode == null)
        {
            return false;
        }

        TreeNode ancestor = this;

        do
        {
            if (ancestor == anotherNode)
            {
                return true;
            }
        } while ((ancestor = ancestor.getParent()) != null);

        return false;
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted
     * at this node in preorder. The first node returned by the
     * enumeration's <code>nextElement()</code> method is this node.
     * <P>
     *
     * Modifying the tree by inserting, removing, or moving a node
     * invalidates any enumerations created before the modification.
     *
     * @see #postorderEnumeration
     * @return an enumeration for traversing the tree in preorder
     */
    public Enumeration<AbstractTreeNode<?>> preorderEnumeration()
    {
        return new PreorderEnumeration(this);
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted
     * at this node in postorder. The first node returned by the
     * enumeration's <code>nextElement()</code> method is the leftmost
     * leaf. This is the same as a depth-first traversal.
     * <P>
     *
     * Modifying the tree by inserting, removing, or moving a node
     * invalidates any enumerations created before the modification.
     *
     * @see #depthFirstEnumeration
     * @see #preorderEnumeration
     * @return an enumeration for traversing the tree in postorder
     */
    public Enumeration<AbstractTreeNode<?>> postorderEnumeration()
    {
        return new PostorderEnumeration(this);
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted
     * at this node in breadth-first order. The first node returned by the
     * enumeration's <code>nextElement()</code> method is this node.
     * <P>
     *
     * Modifying the tree by inserting, removing, or moving a node
     * invalidates any enumerations created before the modification.
     *
     * @see #depthFirstEnumeration
     * @return an enumeration for traversing the tree in breadth-first order
     */
    public Enumeration<AbstractTreeNode<?>> breadthFirstEnumeration()
    {
        return new BreadthFirstEnumeration(this);
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted
     * at this node in depth-first order. The first node returned by the
     * enumeration's <code>nextElement()</code> method is the leftmost
     * leaf. This is the same as a postorder traversal.
     * <P>
     *
     * Modifying the tree by inserting, removing, or moving a node
     * invalidates any enumerations created before the modification.
     *
     * @see #breadthFirstEnumeration
     * @see #postorderEnumeration
     * @return an enumeration for traversing the tree in depth-first order
     */
    public Enumeration<AbstractTreeNode<?>> depthFirstEnumeration()
    {
        return postorderEnumeration();
    }

    /**
     * Creates and returns an enumeration that follows the path from
     * <code>ancestor</code> to this node. The enumeration's
     * <code>nextElement()</code> method first returns
     * <code>ancestor</code>, then the child of <code>ancestor</code>
     * that is an ancestor of this node, and so on, and finally returns this
     * node. Creation of the enumeration is O(m) where m is the number of
     * nodes between this node and <code>ancestor</code>, inclusive. Each
     * <code>nextElement()</code> message is O(1).
     * <P>
     *
     * Modifying the tree by inserting, removing, or moving a node
     * invalidates any enumerations created before the modification.
     *
     * @see #isNodeAncestor
     * @see #isNodeDescendant
     * @exception IllegalArgumentException if <code>ancestor</code> is not
     *                an ancestor of this node
     * @return an enumeration for following the path from an ancestor of
     *         this node to this one
     */
    public Enumeration<AbstractTreeNode<?>> pathFromAncestorEnumeration(final AbstractTreeNode<?> ancestor)
    {
        return new PathBetweenNodesEnumeration(ancestor, this);
    }

    public Enumeration<AbstractTreeNode<?>> pathToRootEnumeration()
    {
        return new PathToRootEnumeration(this);
    }

    /**
     * Builds tree path of the node
     *
     * @return a <code>TreePath</code> object
     */
    public TreePath getTreePath()
    {
        final LinkedList<AbstractTreeNode<?>> l = new LinkedList<AbstractTreeNode<?>>();
        for(AbstractTreeNode<?> node = this; node != null; node = node.getParentNode())
        {
            l.addFirst(node);
        }

        return new TreePath(l.toArray());
    }

    /**
     * Returns the path from the root, to get to this node. The last element
     * in the path is this node.
     *
     * @return an array of TreeNode objects giving the path, where the first
     *         element in the path is the root and the last element is this
     *         node.
     */
    public TreeNode[] getPath()
    {
        return getPathToRoot(this, 0);
    }

    /**
     * Builds the parents of node up to and including the root node, where
     * the original node is the last element in the returned array. The
     * length of the returned array gives the node's depth in the tree.
     *
     * @param aNode the TreeNode to get the path for
     * @param depth an int giving the number of steps already taken towards
     *            the root (on recursive calls), used to size the returned
     *            array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node
     */
    protected TreeNode[] getPathToRoot(final TreeNode aNode, int depth)
    {
        TreeNode[] retNodes;

        /*
         * Check for null, in case someone passed in a null node, or they
         * passed in an element that isn't rooted at root.
         */
        if (aNode == null)
        {
            if (depth == 0)
            {
                return null;
            }
            else
            {
                retNodes = new TreeNode[depth];
            }
        }
        else
        {
            depth++;
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    public Object[] getUserObjectPath()
    {
        LinkedList<Object> list = new LinkedList<Object>();
        for(AbstractTreeNode<?> node = this; node != null; node = node.getParentNode())
        {
            final Object object = node.getObject();
            list.addFirst(object);
        }

        return list.toArray();
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[" + LengthUtils.toString(getObject()) + "]";
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return LengthUtils.hashCode(getObject());
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof AbstractTreeNode<?>)
        {
            final AbstractTreeNode<?> that = (AbstractTreeNode<?>) obj;
            return LengthUtils.equals(this.getObject(), that.getObject());
        }

        return false;
    }

    /**
     * @return
     * @see java.lang.Object#clone()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object clone()
    {
        AbstractTreeNode<UserObjectType> newNode = null;

        try
        {
            newNode = (AbstractTreeNode<UserObjectType>) super.clone();
            newNode.m_model = getModel();
            newNode.m_parent = null;
            newNode.m_children = new LinkedList<AbstractTreeNode<?>>();
            newNode.m_object = new DirectRef<UserObjectType>(getObject());
        }
        catch (final CloneNotSupportedException e)
        {
            // Won't happen because we implement Cloneable
            throw new Error(e);
        }
        return newNode;
    }

    static final class PreorderEnumeration implements Enumeration<AbstractTreeNode<?>>
    {
        protected Stack<Enumeration<AbstractTreeNode<?>>> stack;

        public PreorderEnumeration(final AbstractTreeNode<?> rootNode)
        {
            super();
            stack = new Stack<Enumeration<AbstractTreeNode<?>>>();
            stack.push(new ObjectWrapEnumeration(rootNode));
        }

        public boolean hasMoreElements()
        {
            return !stack.empty() && stack.peek().hasMoreElements();
        }

        public AbstractTreeNode<?> nextElement()
        {
            final Enumeration<AbstractTreeNode<?>> enumer = stack.peek();
            final AbstractTreeNode<?> node = enumer.nextElement();
            final Enumeration<AbstractTreeNode<?>> children = node.getChildren();

            if (!enumer.hasMoreElements())
            {
                stack.pop();
            }
            if (children.hasMoreElements())
            {
                stack.push(children);
            }
            return node;
        }

    } // End of class PreorderEnumeration

    static final class PostorderEnumeration implements Enumeration<AbstractTreeNode<?>>
    {
        protected AbstractTreeNode<?> root;

        protected Enumeration<AbstractTreeNode<?>> children;

        protected Enumeration<AbstractTreeNode<?>> subtree;

        public PostorderEnumeration(final AbstractTreeNode<?> rootNode)
        {
            super();
            root = rootNode;
            children = root.getChildren();
            subtree = EMPTY_ENUMERATION;
        }

        public boolean hasMoreElements()
        {
            return root != null;
        }

        public AbstractTreeNode<?> nextElement()
        {
            AbstractTreeNode<?> retval;

            if (subtree.hasMoreElements())
            {
                retval = subtree.nextElement();
            }
            else if (children.hasMoreElements())
            {
                subtree = new PostorderEnumeration(children.nextElement());
                retval = subtree.nextElement();
            }
            else
            {
                retval = root;
                root = null;
            }

            return retval;
        }
    } // End of class PostorderEnumeration

    static final class BreadthFirstEnumeration implements Enumeration<AbstractTreeNode<?>>
    {
        protected LinkedList<Enumeration<AbstractTreeNode<?>>> queue;

        public BreadthFirstEnumeration(final AbstractTreeNode<?> rootNode)
        {
            super();
            queue = new LinkedList<Enumeration<AbstractTreeNode<?>>>();
            queue.addLast(new ObjectWrapEnumeration(rootNode));
        }

        public boolean hasMoreElements()
        {
            return !queue.isEmpty() && queue.getFirst().hasMoreElements();
        }

        public AbstractTreeNode<?> nextElement()
        {
            final Enumeration<AbstractTreeNode<?>> enumer = queue.getFirst();
            final AbstractTreeNode<?> node = enumer.nextElement();
            final Enumeration<AbstractTreeNode<?>> children = node.getChildren();

            if (!enumer.hasMoreElements())
            {
                queue.removeFirst();
            }
            if (children.hasMoreElements())
            {
                queue.addLast(children);
            }
            return node;
        }

    } // End of class BreadthFirstEnumeration

    static final class PathBetweenNodesEnumeration implements Enumeration<AbstractTreeNode<?>>
    {
        protected Stack<AbstractTreeNode<?>> stack;

        public PathBetweenNodesEnumeration(final AbstractTreeNode<?> ancestor,
                final AbstractTreeNode<?> descendant)
        {
            super();

            if (ancestor == null || descendant == null)
            {
                throw new IllegalArgumentException("argument is null");
            }

            AbstractTreeNode<?> current;

            stack = new Stack<AbstractTreeNode<?>>();
            stack.push(descendant);

            current = descendant;
            while (current != ancestor)
            {
                current = current.getParentNode();
                if (current == null && descendant != ancestor)
                {
                    throw new IllegalArgumentException("node " + ancestor + " is not an ancestor of " + descendant);
                }
                stack.push(current);
            }
        }

        public boolean hasMoreElements()
        {
            return stack.size() > 0;
        }

        public AbstractTreeNode<?> nextElement()
        {
            try
            {
                return stack.pop();
            }
            catch (final EmptyStackException e)
            {
                throw new NoSuchElementException("No more elements");
            }
        }

    } // End of class PathBetweenNodesEnumeration

    static final class EmptyEnumeration implements Enumeration<AbstractTreeNode<?>>
    {
        public boolean hasMoreElements()
        {
            return false;
        }

        public AbstractTreeNode<?> nextElement()
        {
            throw new NoSuchElementException("No more elements");
        }
    };

    static final class ObjectWrapEnumeration implements Enumeration<AbstractTreeNode<?>>
    {
        private AbstractTreeNode<?> m_object;

        public ObjectWrapEnumeration(final AbstractTreeNode<?> object)
        {
            m_object = object;
        }

        public boolean hasMoreElements()
        {
            return m_object != null;
        }

        public AbstractTreeNode<?> nextElement()
        {
            final AbstractTreeNode<?> result = m_object;
            m_object = null;
            return result;
        }
    };

    static final class PathToRootEnumeration implements Enumeration<AbstractTreeNode<?>>
    {
        private AbstractTreeNode<?> m_node;

        /**
         * Constructor
         * @param node
         */
        public PathToRootEnumeration(AbstractTreeNode<?> node)
        {
            m_node = node;
        }

        public boolean hasMoreElements()
        {
            return m_node != null;
        }

        /**
         * @return
         * @see java.util.Enumeration#nextElement()
         */
        public AbstractTreeNode<?> nextElement()
        {
            AbstractTreeNode<?> result = m_node;
            m_node = m_node.getParentNode();
            return result;
        }
    }


}
