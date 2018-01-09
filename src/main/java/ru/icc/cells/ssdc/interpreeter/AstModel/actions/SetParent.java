package ru.icc.cells.ssdc.interpreeter.AstModel.actions;

import ru.icc.cells.ssdc.interpreeter.AstModel.Identifier;

public class SetParent extends Action {

    public SetParent(int id, String name) {
        super(id, name);
    }

    private Identifier parent;

    public void setParent(Identifier parent) {
        this.parent = parent;
    }

    public Identifier getParent() {
        return parent;
    }

    private Identifier child;

    public void setChild(Identifier child) {
        this.child = child;
    }

    public Identifier getChild() {
        return child;
    }

    @Override
    public String toString() {
        return String.format("[ %d %s ( %s, %s) ]", getId(), getName(), parent.toString(), child.toString());
    }

    @Override
    public String generateCallingAction() {

        StringBuilder code = new StringBuilder();

        code.append(getName()).append(getId()).append(".eval( ").append(parent.getNormalForm()).append(", ").append(child.getNormalForm()).append(" )");

        return code.toString();
    }
}