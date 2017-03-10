package entities;

import java.util.Objects;

public class Drug implements Cloneable {
    private String name;
    private String id;

    public Drug() {}

    public Drug(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(this.getId())) return false;

        if (Objects.isNull(obj)) return false;

        Drug other = (Drug) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s', id='%s'}", this.getClass().getName(), getName(), getId());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
