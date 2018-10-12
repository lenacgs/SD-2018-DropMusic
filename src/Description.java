import java.util.*;

public class Description {
    private String text;
    private ArrayList<User> editors;

    public Description(String text, User editor) {
        this.text = text;
        this.editors.add(editor);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void edit(User editor, String text) {
        this.setText(text);
        this.editors.add(editor);
    }


}
