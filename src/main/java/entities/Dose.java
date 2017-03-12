package entities;

public class Dose extends Entity {
    private int quantity;
    private String form;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', quantity='%s'}", this.getClass().getName(), getId(), getQuantity());
    }
}
