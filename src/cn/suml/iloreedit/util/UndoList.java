package cn.suml.iloreedit.util;

import java.util.LinkedList;

public class UndoList<T> {
    private final int max;
    private final LinkedList<T> undo = new LinkedList<>();
    private final LinkedList<T> redo = new LinkedList<>();
    private T current = null;

    public UndoList() {
        this(10);
    }

    public UndoList(int max) {
        this.max = max;
    }

    public void push(T current) {
        if (this.current != null) {
            undo.addLast(this.current);
        }
        this.current = current;
        if (undo.size() > max) {
            undo.removeFirst();
        }
    }

    public T undo() {
        if (undo.isEmpty()) {
            return null;
        }
        redo.addLast(this.current);
        this.current = undo.removeLast();
        return this.current;
    }

    public boolean canUndo() {
        return !undo.isEmpty();
    }

    public T redo() {
        if (redo.isEmpty()) {
            return null;
        }
        push(redo.removeLast());
        return this.current;
    }

    public boolean canRedo() {
        return !redo.isEmpty();
    }

    public T getCurrent() {
        return current;
    }

}
