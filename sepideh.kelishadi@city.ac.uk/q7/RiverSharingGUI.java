import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RiverSharingGUI extends JFrame {

    //We have button for actions : raiseFlag, setIndicator, lowerFlag, checkFlag&Visit
    //and we have labels for showing the status of flags and the indicator
    // and an extra label for showing the result of each action
    private final JLabel hatfieldsFlagStatus;
    private final JLabel mccoysFlagStatus;
    private final JLabel turnIndicator;
    private final JLabel result;
    private final JButton hatfieldsRaiseFlagButton;
    private final JButton hatfieldsCheckFlagButton;
    private final JButton hatfieldsLowerFlagButton;
    private final JButton hatfieldsSetIndicatorButton;
    private final JButton mccoysRaiseFlagButton;
    private final JButton mccoysCheckFlagButton;
    private final JButton mccoysLowerFlagButton;
    private final JButton mccoysSetIndicatorButton;
    private final RiverAccessMonitor monitor;

    public RiverSharingGUI() {
        super("River Sharing Protocol");

        monitor = new RiverAccessMonitor(this);

        hatfieldsFlagStatus = new JLabel("Hatfields Flag: Lowered");
        mccoysFlagStatus = new JLabel("McCoys Flag: Lowered");
        turnIndicator = new JLabel("Turn: None");
        result = new JLabel("");

        hatfieldsRaiseFlagButton = new JButton("Hatfields Raise Flag");
        hatfieldsSetIndicatorButton = new JButton("Hatfields Set Indicator");
        hatfieldsCheckFlagButton = new JButton("Hatfields Check & Visit");
        hatfieldsLowerFlagButton = new JButton("Hatfields Lower Flag");

        mccoysRaiseFlagButton = new JButton("McCoys Raise Flag");
        mccoysSetIndicatorButton = new JButton("McCoys Set Indicator");
        mccoysCheckFlagButton = new JButton("McCoys Check & Visit");
        mccoysLowerFlagButton = new JButton("McCoys Lower Flag");

        // Button listeners
        hatfieldsRaiseFlagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.raiseFlag(true);
            }
        });

        hatfieldsCheckFlagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.checkAndVisit(true);
            }
        });

        hatfieldsLowerFlagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.lowerFlag(true);
            }
        });

        hatfieldsSetIndicatorButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.setTurn(false); // Passing the turn to McCoys
            }
        });

        mccoysRaiseFlagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.raiseFlag(false);
            }
        });

        mccoysCheckFlagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.checkAndVisit(false);
            }
        });

        mccoysLowerFlagButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.lowerFlag(false);
            }
        });

        mccoysSetIndicatorButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                monitor.setTurn(true); // Passing the turn to Hatfields
            }
        });

        setLayout(new GridLayout(0, 1));
        add(hatfieldsFlagStatus);
        add(hatfieldsRaiseFlagButton);
        add(hatfieldsCheckFlagButton);
        add(hatfieldsLowerFlagButton);
        add(hatfieldsSetIndicatorButton);
        add(mccoysFlagStatus);
        add(mccoysRaiseFlagButton);
        add(mccoysCheckFlagButton);
        add(mccoysLowerFlagButton);
        add(mccoysSetIndicatorButton);
        add(turnIndicator);
        add(result);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void updateFlagStatus(final boolean isHatfields, final boolean raised) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (isHatfields) {
                    hatfieldsFlagStatus.setText("Hatfields Flag: " + (raised ? "Raised" : "Lowered"));
                } else {
                    mccoysFlagStatus.setText("McCoys Flag: " + (raised ? "Raised" : "Lowered"));
                }
            }
        });
    }

    public void updateTurnIndicator(final int turn) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                String whoseTurn = turn == 1 ? "Hatfields" : "McCoys";
                turnIndicator.setText("Turn: " + whoseTurn);
            }
        });
    }
    public void updateResult(final String results) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                result.setText(results);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new RiverSharingGUI();
            }
        });
    }
}

class RiverAccessMonitor {

    //We have 2 FLAGS and an Indicator for changing turns
    private boolean hatfieldsFlag = false;
    private boolean mccoysFlag = false;
    private int turn = 1; // 1 for Hatfields, 2 for McCoys
    private final RiverSharingGUI gui;

    public RiverAccessMonitor(RiverSharingGUI gui) {
        this.gui = gui;
    }

    //raiseFlag action changes the 3th and 4th input of the RIVER process
    //and shows how wants to enter the river
    public synchronized void raiseFlag(boolean isHatfields) {
        if (isHatfields) {
            hatfieldsFlag = true;
            gui.updateFlagStatus(true, true);
        } else {
            mccoysFlag = true;
            gui.updateFlagStatus(false, true);
        }
    }

    public synchronized void checkAndVisit(boolean isHatfields) {
        //If hatfiled raise the flag
        if (isHatfields) {
            //If macCoys flag is raised and its macCoys turn(Indicator=2) hatfield must wait
            if (mccoysFlag && turn == 2) {
                gui.updateResult("Hatfields cannot visit now(WAIT!!).");
            } else {
                //other wise they can enter
                gui.updateResult("Hatfields are visiting the river.");
            }
        }
        //If macCoys raise the flag
        else {
            //If hatfields flag is raised and its hatfileds turn(Indicator=1) hatfield must wait
            if (hatfieldsFlag && turn == 1) {
                gui.updateResult("McCoys cannot visit now.");
            } else {
                gui.updateResult("McCoys are visiting the river.");
            }
        }
    }

    //lowerFlag action makes the boolean range to False in the RIVER process
    public synchronized void lowerFlag(boolean isHatfields) {
        if (isHatfields) {
            hatfieldsFlag = false;
            gui.updateFlagStatus(true, false);
            gui.updateResult("Hatfields have left the river.");
        } else {
            mccoysFlag = false;
            gui.updateFlagStatus(false, false);
            gui.updateResult("McCoys have left the river.");
        }
    }

    //setTurn is setIndicator action in the FSP Model and change clans turns each time
    public synchronized void setTurn(boolean toHatfields) {
        turn = toHatfields ? 1 : 2;
        gui.updateTurnIndicator(turn);
        notifyAll();
    }
}
