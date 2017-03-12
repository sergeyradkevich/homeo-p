package entities;

import java.util.Objects;

public class Entity implements Cloneable {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(this.getId())) return false;

        if (Objects.isNull(obj)) return false;

        Entity other = (Entity) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
