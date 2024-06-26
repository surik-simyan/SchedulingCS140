import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {

    private JPanel screen = new JPanel(), tools = new JPanel();
    private JButton tool[];
    private JTextField field[];
    private CourseArray courses;
    private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};

    private Autoassociator autoassociator;

    public TimeTable() {
        super("Dynamic Time Table");
        setSize(500, 800);
        setLayout(new FlowLayout());

        screen.setPreferredSize(new Dimension(400, 800));
        add(screen);

        setTools();
        add(tools);

        setVisible(true);
    }

    public void setTools() {
        String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
        field = new JTextField[capField.length];

        String capButton[] = {"Load", "Start", "Step", "Continue", "Print", "Exit"};
        tool = new JButton[capButton.length];

        tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));

        for (int i = 0; i < field.length; i++) {
            tools.add(new JLabel(capField[i]));
            field[i] = new JTextField(5);
            tools.add(field[i]);
        }

        for (int i = 0; i < tool.length; i++) {
            tool[i] = new JButton(capButton[i]);
            tool[i].addActionListener(this);
            tools.add(tool[i]);
        }

        field[0].setText("17");
        field[1].setText("139");
        field[2].setText("sta-f-83.stu");
        field[3].setText("1");
    }

    public void draw() {
        Graphics g = screen.getGraphics();
        int width = Integer.parseInt(field[0].getText()) * 10;
        for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
            g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
            g.drawLine(0, courseIndex, width, courseIndex);
            g.setColor(CRScolor[CRScolor.length - 1]);
            g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
        }
    }

    private int getButtonIndex(JButton source) {
        int result = 0;
        while (source != tool[result]) result++;
        return result;
    }

    public void actionPerformed(ActionEvent click) {
        int min, step, clashes;

        switch (getButtonIndex((JButton) click.getSource())) {
            case 0:
                int slots = Integer.parseInt(field[0].getText());
                courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
                courses.readClashes(field[2].getText());
                autoassociator = new Autoassociator(courses);
                draw();
                break;
            case 1:
                trainAutoassociator();
                min = Integer.MAX_VALUE;
                step = 0;
                for (int i = 1; i < courses.length(); i++) courses.setSlot(i, 0);

                for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
                    courses.iterate(Integer.parseInt(field[4].getText()));
                    draw();
                    clashes = courses.clashesLeft();
                    if (clashes < min) {
                        min = clashes;
                        step = iteration;
                    }
                }
                System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
                setVisible(true);
                break;
            case 2:
                courses.iterate(Integer.parseInt(field[4].getText()));
                draw();
                break;
            case 3:
                min = Integer.MAX_VALUE;
                step = 0;
                for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
                    courses.iterate(Integer.parseInt(field[4].getText()) - step);
                    draw();
                    clashes = courses.clashesLeft();
                    if (clashes < min) {
                        min = clashes;
                        step = iteration;
                    }
                }
                System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
                setVisible(true);
                break;
            case 4:
                System.out.println("Exam\tSlot\tClashes");
                for (int i = 1; i < courses.length(); i++)
                    System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
                break;
            case 5:
                System.exit(0);
        }
    }

    private void trainAutoassociator() {
        int[] clashFreeSlots = findClashFreeSlots();
        autoassociator.training(clashFreeSlots);

        saveLog(clashFreeSlots);
    }

    private int[] findClashFreeSlots() {
        int[] clashFreeSlots = {1, 2, 3, 4};
        return clashFreeSlots;
    }

    private void saveLog(int[] clashFreeSlots) {
        try (FileWriter writer = new FileWriter("timeslots.txt")) {
            writer.write("Number of slots: " + field[0].getText() + "\n");
            writer.write("Shift: " + field[4].getText() + "\n");
            writer.write("Iteration index: " + field[3].getText() + "\n");

            writer.write("Timeslot index: ");
            for (int i = 0; i < clashFreeSlots.length; i++) {
                writer.write(clashFreeSlots[i] + " ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TimeTable();
    }
}
