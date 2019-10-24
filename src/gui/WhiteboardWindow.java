package gui;

import sharedCode.WhiteboardListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class WhiteboardWindow extends JFrame{

    // Sizes
    private final int WINDOW_POS_X = 50;
    private final int WINDOW_POS_Y = 50;
    public final int MAIN_PANEL_WIDTH = 1100;
    public final int MAIN_PANEL_HEIGHT = 700;
    public final int TOOL_WIDTH = 150;
    public final int TOOL_TOGGLE_DIMENSION = 45;
    public final int CANVAS_WIDTH = 690;
    public final int CANVAS_HEIGHT = 720;
    public final int CHAT_WIDTH = 250;
    public final int MESSAGE_HEIGHT = 601;
    public final int INPUT_HEIGHT = 100;
    public final int NUM_OF_TOOLS = 7;
    public final int NUM_OF_COLOUR_BTNS = 16;

    // Main frame
    private JFrame frame;

    // Menu
    private JMenuItem mntmNew;
    private JMenuItem mntmOpen;
    private JMenuItem mntmSave;
    private JMenuItem mntmSaveAs;
    private JMenuItem mntmClose;
    private JMenuItem mntmConnect;
    private JMenuItem mntmDisconnect;

    // Tools
    private ArrayList<JToggleButton> paintTools;
    private ArrayList<JToggleButton> colours;
    private JButton sizeBtn;
    private JButton paletteBtn;

    // Canvas
    private JPanel canvasPanel;

    // Chat
    private JTabbedPane communicationTabbedPane;
    private JTextPane tpUsersMessages;
    private JTextArea taInputMessage;
    private JButton btnPost;
    private JScrollPane viewUsersScrollPane;

    //
    private WhiteboardListener wl;


    public WhiteboardWindow(WhiteboardListener wl) {
        this.wl = wl;
        initialiseWindow();
    }

    public void showWindow() {
        frame.pack();
        frame.setVisible(true);
        wl.setCanvas(canvasPanel);
        setGuiToConnected();
    }

    private void initialiseWindow() {
        initialiseMainFrame();
        initialiseMenu();
        initialiseTools();
        initialiseCanvas();
        initialiseChatPanel();
    }

    private void initialiseMainFrame() {
        frame = new JFrame();
        frame.setBounds(WINDOW_POS_X, WINDOW_POS_Y, MAIN_PANEL_WIDTH, MAIN_PANEL_HEIGHT);
        frame.setResizable(false);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));
        frame.setTitle("Whiteboard");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int answer;
                if (wl.getIsManager()) {
                    answer = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to close this window? All users will be kicked out of the room.",
                            "Close Window?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                } else {
                    answer = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to close this window? Your work will not be saved unless it is saved by other collaborators.",
                            "Close Window?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                }
                if (answer == 0) {
                    if (wl.disconnectFromServer()) {
                        System.exit(0);
                    }
                }
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
                System.out.println("Window minimised");
//                wl.getboardfromServer(100);
                Thread queryThread = new Thread() {
                    public void run() {
                            wl.setCanvas(canvasPanel);
                            System.out.println("connected");
                            wl.getboardfromServer(100);

                    }
                };
                queryThread.start();
            }
        });
    }

    private void initialiseMenu() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // -------------- File ----------------
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        // Create new canvas
        mntmNew = new JMenuItem("New");
        mntmNew.addActionListener(wl);
        mnFile.add(mntmNew);

        // Open local image
        mntmOpen = new JMenuItem("Open");
        mntmOpen.addActionListener(wl);
        mnFile.add(mntmOpen);

        // Save current canvas
        mntmSave = new JMenuItem("Save");
        mntmSave.addActionListener(wl);
        mnFile.add(mntmSave);
        mntmSaveAs = new JMenuItem("Save as");
        mntmSaveAs.addActionListener(wl);
        mnFile.add(mntmSaveAs);

        // Close the application
        mntmClose = new JMenuItem("Close");
        mntmClose.addActionListener(wl);
        mnFile.add(mntmClose);

        // -------------- Connection ----------------
        JMenu mnConnection = new JMenu("Connection");
        menuBar.add(mnConnection);

        // Connect to the server
        mntmConnect = new JMenuItem("Connect");
        mntmConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread queryThread = new Thread() {
                    public void run() {
                        boolean status = wl.connectToServer(false);
                        if (status) {
                            setGuiToConnected();
                            wl.setCanvas(canvasPanel);
                            if (wl.getReconnect()){
                                System.out.println("connected");
                                wl.getboardfromServer(100);
                            }
                        }
                    }
                };
                queryThread.start();
            }
        });
        mnConnection.add(mntmConnect);

        // Disconnect to the server
        mntmDisconnect = new JMenuItem("Disconnect");
        mntmDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean confirm = wl.confirmDisconnection();
                if (confirm) {
                    boolean status = wl.disconnectFromServer();
                    if (!status) {
                        JOptionPane.showMessageDialog(null,"Disconnection failed.");
                    } else {
                        JOptionPane.showConfirmDialog(null,"You have successfully disconnected from the room.","",JOptionPane.DEFAULT_OPTION);
                        setGuiToDisconnected();
                    }
                }
            }
        });
        mnConnection.add(mntmDisconnect);

        // -------------- Team information ----------------
        JMenu mnAbout = new JMenu("About");
        menuBar.add(mnAbout);
        JMenuItem mntmShibaSquad = new JMenuItem("About us...");
        mnAbout.add(mntmShibaSquad);
        mntmShibaSquad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = new ImageIcon("src/assets/shiba.png");
                JOptionPane.showMessageDialog(null, "Hi, we are Shiba Squad.", "Hello",
                        JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
    }

    private void initialiseTools() {
        final int TOOL_GRID_WIDTH = 2;

        JPanel toolPanel = new JPanel();
        frame.getContentPane().add(toolPanel);
        GridBagLayout gbl_toolPanel = new GridBagLayout();
        toolPanel.setLayout(gbl_toolPanel);
        toolPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        toolPanel.setPreferredSize(new Dimension(TOOL_WIDTH, MAIN_PANEL_HEIGHT));

        // ---------------- Paint tools ----------------
        final int TOOL_BLOCK_1_OFFSET = 1;
        // label
        JLabel lblpt = new JLabel("Paint Tools");
        GridBagConstraints gbc_lblpt = new GridBagConstraints();
        gbc_lblpt.gridwidth = TOOL_GRID_WIDTH;
        gbc_lblpt.insets = new Insets(0, 0, 10, 0);
        gbc_lblpt.gridx = 0;
        gbc_lblpt.gridy = 0;
        toolPanel.add(lblpt, gbc_lblpt);
        // toggle buttons
        paintTools = new ArrayList<JToggleButton>();
        int col = 0; int row = 0;
        for (int index = 0; index<NUM_OF_TOOLS; index++)
        {
            JToggleButton tb = new JToggleButton();
            paintTools.add(tb);
            String cmd = wl.getPaintToolStr(index);
            if (cmd == "BRUSH") tb.setSelected(true); // default tool
            tb.setActionCommand(cmd);
            tb.setPreferredSize(new Dimension(TOOL_TOGGLE_DIMENSION, TOOL_TOGGLE_DIMENSION));
            tb.setFont(new Font("Arial", Font.PLAIN, 10));
            try {
                ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getResource("/assets/tools/" + cmd + ".png")));
                tb.setIcon(icon);
            } catch (Exception e) {
                tb.setText(cmd.substring(0, 4));
            }
            // Set GridBagConstraints
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = col % TOOL_GRID_WIDTH;
            gbc.gridy = row + TOOL_BLOCK_1_OFFSET;
            if (col % TOOL_GRID_WIDTH == 0) {
                gbc.insets = new Insets(0, 0, 6, 6);
            } else {
                gbc.insets = new Insets(0, 0, 6, 0);
            }
            toolPanel.add(tb, gbc);
            // ActionListener
            tb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    wl.toolBtnClicked(e.getActionCommand());
                    // unselect other toggle buttons
                    for (JToggleButton this_tb : paintTools) {
                        if (this_tb.getActionCommand() != e.getActionCommand()) {
                            this_tb.setSelected(false);
                        }
                    }
                }
            });
            // Next location
            col++;
            if (col % TOOL_GRID_WIDTH == 0) row++;
        }

        // ---------------- Size ----------------
        sizeBtn = new JButton();
        sizeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel sizePanel = new JPanel();

                JSlider strokeSlider = new JSlider(0,20,1);
                strokeSlider.setMajorTickSpacing(5);
                strokeSlider.setPaintTicks(true);
                strokeSlider.setPaintLabels(true);
                strokeSlider.setValue(wl.getStroke());
                JSlider fontSlider = new JSlider(0,50,12);
                fontSlider.setMajorTickSpacing(10);
                fontSlider.setPaintTicks(true);
                fontSlider.setPaintLabels(true);
                fontSlider.setValue(wl.getFontSize());

                sizePanel.add(new JLabel("Brush Stroke:"));
                sizePanel.add(strokeSlider);
                sizePanel.add(new JLabel("Font Size:"));
                sizePanel.add(fontSlider);

                int result = JOptionPane.showConfirmDialog(null,sizePanel,
                        "Please choose the brush stroke and font size:",JOptionPane.OK_CANCEL_OPTION);
                if(result == JOptionPane.OK_OPTION) {
                    wl.sizeBtnClicked(strokeSlider.getValue(), fontSlider.getValue());
                }
            }
        });
        sizeBtn.setPreferredSize(new Dimension(TOOL_TOGGLE_DIMENSION, TOOL_TOGGLE_DIMENSION));
        sizeBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getResource("/assets/tools/SIZE.png")));
            sizeBtn.setIcon(icon);
        } catch (Exception e) {
            sizeBtn.setText("SIZE");
        }
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = col % TOOL_GRID_WIDTH;
        gbc1.gridy = row + TOOL_BLOCK_1_OFFSET;
        if (col % TOOL_GRID_WIDTH == 0)
        {
            gbc1.insets = new Insets(0, 0, 6, 6);
        }
        else
        {
            gbc1.insets = new Insets(0, 0, 6, 0);
        }
        toolPanel.add(sizeBtn, gbc1);


        // ---------------- Colours ----------------
        final int TOOL_BLOCK_2_OFFSET = paintTools.size() / TOOL_GRID_WIDTH + (paintTools.size() % 2) + TOOL_BLOCK_1_OFFSET + 1;
        // label
        JLabel lblColours = new JLabel("Colours");
        GridBagConstraints gbc_lblColours = new GridBagConstraints();
        gbc_lblColours.insets = new Insets(0, 0, 10, 0);
        gbc_lblColours.gridwidth = TOOL_GRID_WIDTH;
        gbc_lblColours.gridx = 0;
        gbc_lblColours.gridy = TOOL_BLOCK_2_OFFSET;
        toolPanel.add(lblColours, gbc_lblColours);
        // toggle buttons
        colours = new ArrayList<JToggleButton>();
        colours.add(new JToggleButton("Red"));
        colours.add(new JToggleButton("Blue"));
        colours.add(new JToggleButton("Gree"));
        colours.add(new JToggleButton("Yell"));
        colours.add(new JToggleButton("Oran"));
        colours.add(new JToggleButton("LtBl"));
        colours.add(new JToggleButton("LtGr"));
        colours.add(new JToggleButton("Purp"));
        colours.add(new JToggleButton("Pink"));
        colours.add(new JToggleButton("Cyan"));
        colours.add(new JToggleButton("Tan"));
        colours.add(new JToggleButton("Brow"));
        colours.add(new JToggleButton("Blac"));
        colours.add(new JToggleButton("DkGy"));
        colours.add(new JToggleButton("LtGy"));
        colours.add(new JToggleButton("Whit"));
        col = 0; row = 0; int index = 0;
        for (JToggleButton tb : colours)
        {
            String cmd = wl.getColourStr(index);
            if (cmd == "BLACK") tb.setSelected(true);
            tb.setActionCommand(cmd);
            tb.setPreferredSize(new Dimension(TOOL_TOGGLE_DIMENSION, TOOL_TOGGLE_DIMENSION));
            tb.setOpaque(true);
            tb.setBackground(wl.getColourByStr(wl.getColourStr(index)));
            tb.setFont(new Font("Arial", Font.PLAIN, 10));
            // Set GridBagConstraints
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = col % TOOL_GRID_WIDTH;
            gbc.gridy = row + TOOL_BLOCK_2_OFFSET + 1;
            if (col % TOOL_GRID_WIDTH == 0)
            {
                gbc.insets = new Insets(0, 0, 6, 6);
            }
            else
            {
                gbc.insets = new Insets(0, 0, 6, 0);
            }
            toolPanel.add(tb, gbc);
            // ActionListener
            tb.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    wl.colourBtnClicked(e.getActionCommand());
                    // unselect other toggle buttons
                    for (JToggleButton this_tb : colours) {
                        if (this_tb.getActionCommand() != e.getActionCommand()) {
                            this_tb.setSelected(false);
                        }
                    }
                }
            });
            // Next location
            index++;
            col++;
            if (col % TOOL_GRID_WIDTH == 0) row++;
        }

        // ---------------- Palette ----------------
        paletteBtn = new JButton();
        paletteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(toolPanel, "Color Palette", Color.black);
                wl.paletteBtnClicked(color);
                for (JToggleButton tb : colours) {
                    tb.setSelected(false);
                }
            }
        });
        paletteBtn.setPreferredSize(new Dimension(TOOL_TOGGLE_DIMENSION, TOOL_TOGGLE_DIMENSION));
        paletteBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(getClass().getResource("/assets/palette.png")));
            paletteBtn.setIcon(icon);
        } catch (Exception e) {
            paletteBtn.setText("Palette");
        }
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col % TOOL_GRID_WIDTH;
        gbc.gridy = row + TOOL_BLOCK_2_OFFSET + 1;
        if (col % TOOL_GRID_WIDTH == 0)
        {
            gbc.insets = new Insets(0, 0, 6, 6);
        }
        else
        {
            gbc.insets = new Insets(0, 0, 6, 0);
        }
        toolPanel.add(paletteBtn, gbc);
    }

    private void initialiseCanvas() {
        canvasPanel = new JPanel();
        frame.getContentPane().add(canvasPanel);
        canvasPanel.setPreferredSize(new Dimension(CANVAS_WIDTH, MAIN_PANEL_HEIGHT));
        canvasPanel.setMinimumSize(new Dimension(CANVAS_WIDTH, MAIN_PANEL_HEIGHT));
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.addMouseListener(wl);
        canvasPanel.addMouseMotionListener(wl);
    }

    private void initialiseChatPanel() {
        communicationTabbedPane = new JTabbedPane();
        communicationTabbedPane.setBorder(BorderFactory.createLineBorder(Color.black));
        // Chat tab
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        // Messages
        tpUsersMessages = new JTextPane();
        tpUsersMessages.setEditable(false);
        tpUsersMessages.setAutoscrolls(true);
        JScrollPane messageScrollPane = new JScrollPane(tpUsersMessages);
        messageScrollPane.setPreferredSize(new Dimension(CHAT_WIDTH, MESSAGE_HEIGHT));
        GridBagConstraints gbc_messageScrollPane = new GridBagConstraints();
        gbc_messageScrollPane.fill = GridBagConstraints.BOTH;
        gbc_messageScrollPane.gridwidth = 2;
        gbc_messageScrollPane.gridx = 0;
        gbc_messageScrollPane.gridy = 0;
        chatPanel.add(messageScrollPane, gbc_messageScrollPane);
        // Input
        taInputMessage = new JTextArea();
        taInputMessage.setLineWrap(true);
        JScrollPane inputScrollPane = new JScrollPane(taInputMessage);
        inputScrollPane.setMinimumSize(new Dimension(CHAT_WIDTH, INPUT_HEIGHT));
        inputScrollPane.setPreferredSize(new Dimension(CHAT_WIDTH, INPUT_HEIGHT));
        inputScrollPane.setMaximumSize(new Dimension(CHAT_WIDTH, INPUT_HEIGHT));
        inputScrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
        GridBagConstraints gbc_inputScrollPane = new GridBagConstraints();
        gbc_inputScrollPane.fill = GridBagConstraints.BOTH;
        gbc_inputScrollPane.gridx = 0;
        gbc_inputScrollPane.gridy = 1;
        chatPanel.add(inputScrollPane, gbc_inputScrollPane);
        // Post button
        btnPost = new JButton("Post");
        btnPost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                wl.postMessage(taInputMessage.getText());
                taInputMessage.setText("");
            }
        });
        GridBagConstraints gbc_btnPost = new GridBagConstraints();
        gbc_btnPost.fill = GridBagConstraints.BOTH;
        gbc_btnPost.gridx = 1;
        gbc_btnPost.gridy = 1;
        chatPanel.add(btnPost, gbc_btnPost);
//        dl.setChatPanel(taInputMessage, tpUsersMessages);
        communicationTabbedPane.addTab("Chat", chatPanel);

        // View users tab
        viewUsersScrollPane = new JScrollPane();
        viewUsersScrollPane.setMinimumSize(new Dimension(CHAT_WIDTH, MAIN_PANEL_HEIGHT));
        viewUsersScrollPane.setPreferredSize(new Dimension(CHAT_WIDTH, MAIN_PANEL_HEIGHT));
        viewUsersScrollPane.setMaximumSize(new Dimension(CHAT_WIDTH, MAIN_PANEL_HEIGHT));
        communicationTabbedPane.addTab("Online users", viewUsersScrollPane);
        viewUsersScrollPane.getViewport().setName("tab");
//        viewUsersScrollPane.getViewport().addChangeListener(dl);
        frame.getContentPane().add(communicationTabbedPane);
    }

    public void setGuiToConnected() {
        System.out.println("reconnecting......");
        if (wl.getIsManager()) { // connected manager
            mntmNew.setEnabled(true);
            mntmOpen.setEnabled(true);
            mntmSave.setEnabled(true);
            mntmSaveAs.setEnabled(true);
            mntmClose.setEnabled(true);
            mntmConnect.setEnabled(false);
            mntmDisconnect.setEnabled(true);
        } else { // connected user
            mntmNew.setEnabled(false);
            mntmOpen.setEnabled(false);
            mntmSave.setEnabled(true);
            mntmSaveAs.setEnabled(true);
            mntmClose.setEnabled(true);
            mntmConnect.setEnabled(false);
            mntmDisconnect.setEnabled(true);
        }
        // enable all toggle buttons in tool panel
        for (JToggleButton tb : paintTools) {
            tb.setEnabled(true);
        }
        for (JToggleButton tb : colours) {
            tb.setEnabled(true);
        }
        sizeBtn.setEnabled(true);
        paletteBtn.setEnabled(true);
        // enable canvas
        canvasPanel.setEnabled(true);
        // enable chat
        communicationTabbedPane.setEnabledAt(1, true);
        taInputMessage.setEnabled(true);
        btnPost.setEnabled(true);
    }

    public void setGuiToDisconnected() {
        if (wl.getIsManager()) // disconnected manager
        {
            mntmNew.setEnabled(true);
            mntmOpen.setEnabled(true);
            mntmSave.setEnabled(false);
            mntmSaveAs.setEnabled(false);
            mntmClose.setEnabled(true);
            mntmConnect.setEnabled(false);
            mntmDisconnect.setEnabled(false);
        } else // disconnected user
        {
            mntmNew.setEnabled(false);
            mntmOpen.setEnabled(false);
            mntmSave.setEnabled(false);
            mntmSaveAs.setEnabled(false);
            mntmClose.setEnabled(true);
            mntmConnect.setEnabled(true);
            mntmDisconnect.setEnabled(false);
        }
        // disable all toggle buttons in tool panel
        for (JToggleButton tb : paintTools) {
            tb.setEnabled(false);
        }
        for (JToggleButton tb : colours) {
            tb.setEnabled(false);
        }
        sizeBtn.setEnabled(false);
        paletteBtn.setEnabled(false);
        // disable canvas
        canvasPanel.setEnabled(false);
        // disable chat
        communicationTabbedPane.setEnabledAt(1, false);
        taInputMessage.setEnabled(false);
        btnPost.setEnabled(false);
    }

    public void appendTextToMessages(String newText) {
        tpUsersMessages.setText(tpUsersMessages.getText() + newText + "\n");
    }

    public void displayAllMessages(ArrayList<String> messages) {
        tpUsersMessages.setText("");
        for (String msg : messages) {
            tpUsersMessages.setText(tpUsersMessages.getText() + msg + "\n");
        }
    }

    public void displayOnlineUsers(String managerName, ArrayList<String> userlist) {
        System.out.println(wl.getUsername() + " GUI display online users");
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        final int GRID_WIDTH = 4;
        final int USERNAME_WIDTH;
        if (wl.getIsManager())
        {
            USERNAME_WIDTH = 3;
        }
        else
        {
            USERNAME_WIDTH = GRID_WIDTH;
        }
        final int KICK_BTN_WIDTH = 1;
        final int OFFSET = 2;

        // user
        String myName = wl.getUsername();
        JLabel myNameLabel = new JLabel("Your username: " + myName);
        GridBagConstraints gbc_myNameLabel = new GridBagConstraints();
        gbc_myNameLabel.gridx = 0;
        gbc_myNameLabel.gridy = 0;
        gbc_myNameLabel.gridwidth = GRID_WIDTH;
        gbc_myNameLabel.insets = new Insets(20, 20, 20, 20);
        panel.add(myNameLabel, gbc_myNameLabel);

        // manager
        JLabel managerNameLabel = new JLabel("Manager: " + managerName);
        GridBagConstraints gbc_managerNameLabel = new GridBagConstraints();
        gbc_managerNameLabel.gridx = 0;
        gbc_managerNameLabel.gridy = 1;
        gbc_managerNameLabel.gridwidth = GRID_WIDTH;
        gbc_managerNameLabel.insets = new Insets(20, 20, 20, 20);
        panel.add(managerNameLabel, gbc_managerNameLabel);

        if (userlist == null) {
            this.viewUsersScrollPane.setViewportView(panel);
            return;
        }
        int row = 0;
        for (String username : userlist)
        {
            if (!username.equals(myName)) {
                JLabel usernameLabel = new JLabel(username);
                GridBagConstraints gbc_usernameLabel = new GridBagConstraints();
                gbc_usernameLabel.gridx = 0;
                gbc_usernameLabel.gridy = row + OFFSET;
                gbc_usernameLabel.gridwidth = USERNAME_WIDTH;
                gbc_usernameLabel.insets = new Insets(6, 6, 6, 6);
                panel.add(usernameLabel, gbc_usernameLabel);

                if (wl.getIsManager())
                {
                    JButton kickUserBtn = new JButton("kick");
                    kickUserBtn.setActionCommand(username);
                    kickUserBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            wl.kickUser(e.getActionCommand());
                        }
                    });
                    GridBagConstraints gbc_kickUserBtn = new GridBagConstraints();
                    gbc_kickUserBtn.gridx = USERNAME_WIDTH;
                    gbc_kickUserBtn.gridy = row + OFFSET;
                    gbc_kickUserBtn.gridwidth = KICK_BTN_WIDTH;
                    panel.add(kickUserBtn, gbc_kickUserBtn);
                }

                // Next location
                row++;
            }
        }
        this.viewUsersScrollPane.setViewportView(panel);
    }

}
