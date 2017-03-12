package entities;

public class Drug extends Entity {
    private String name;

    public Drug() {}

    public Drug(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s', id='%s'}", this.getClass().getName(), getName(), getId());
    }
}
