package Entity;

public class LRTree {
    private int id;
    private String name;
    private String[] childId;
    private int pos;
    public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getChildId() {
        return childId;
    }
    public void setChildId(String[] childId) {
        this.childId = childId;
    }

}
