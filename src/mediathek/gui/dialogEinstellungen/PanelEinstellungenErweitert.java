/*    
 *    MediathekView
 *    Copyright (C) 2008   W. Xaver
 *    W.Xaver[at]googlemail.com
 *    http://zdfmediathk.sourceforge.net/
 *    
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mediathek.gui.dialogEinstellungen;

import com.jidesoft.utils.SystemInfo;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mSearch.tool.MSLog;
import mediathek.daten.Daten;
import mediathek.gui.PanelVorlage;
import mediathek.gui.dialog.DialogHilfe;
import mediathek.res.GetIcon;
import mediathek.tool.GuiFunktionen;
import mediathek.tool.Konstanten;
import mediathek.tool.ListenerMediathekView;
import mediathek.tool.MVConfig;
import mediathek.tool.MVFunctionSys;
import mediathek.tool.MVFunctionSys.OperatingSystemType;
import mediathek.tool.MVMessageDialog;

public class PanelEinstellungenErweitert extends PanelVorlage {

    public PanelEinstellungenErweitert(Daten d, JFrame pparentComponent) {
        super(d, pparentComponent);
        initComponents();
        daten = d;

        init();
        setIcon();
        setHelp();

        jRadioButtonAuto.addActionListener(e -> setUserAgent());
        jRadioButtonManuel.addActionListener(e -> setUserAgent());

        jTextFieldUserAgent.getDocument().addDocumentListener(new BeobUserAgent());
        ListenerMediathekView.addListener(new ListenerMediathekView(ListenerMediathekView.EREIGNIS_PROGRAMM_OEFFNEN, PanelEinstellungenErweitert.class.getSimpleName()) {
            @Override
            public void ping() {
                init();
            }
        });
        jCheckBoxAboSuchen.setSelected(Boolean.parseBoolean(Daten.mVConfig.get(MVConfig.SYSTEM_ABOS_SOFORT_SUCHEN)));
        jCheckBoxAboSuchen.addActionListener(e -> Daten.mVConfig.add(MVConfig.SYSTEM_ABOS_SOFORT_SUCHEN, Boolean.toString(jCheckBoxAboSuchen.isSelected())));
        jCheckBoxDownloadSofortStarten.setSelected(Boolean.parseBoolean(Daten.mVConfig.get(MVConfig.SYSTEM_DOWNLOAD_SOFORT_STARTEN)));
        jCheckBoxDownloadSofortStarten.addActionListener(e -> Daten.mVConfig.add(MVConfig.SYSTEM_DOWNLOAD_SOFORT_STARTEN, Boolean.toString(jCheckBoxDownloadSofortStarten.isSelected())));

        // ====================================
        jButtonProgrammDateimanager.addActionListener(new BeobPfad(MVConfig.SYSTEM_ORDNER_OEFFNEN, "Dateimanager suchen", jTextFieldProgrammDateimanager));
        jButtonProgrammVideoplayer.addActionListener(new BeobPfad(MVConfig.SYSTEM_PLAYER_ABSPIELEN, "Videoplayer suchen", jTextFieldVideoplayer));
        jButtonProgrammUrl.addActionListener(new BeobPfad(MVConfig.SYSTEM_URL_OEFFNEN, "Browser suchen", jTextFieldProgrammUrl));
        jButtonProgrammShutdown.addActionListener(new BeobPfad(MVConfig.SYSTEM_LINUX_SHUTDOWN, "Shutdown Befehl", jTextFieldProgrammShutdown));

        jTextFieldProgrammDateimanager.setText(Daten.mVConfig.get(MVConfig.SYSTEM_ORDNER_OEFFNEN));
        jTextFieldProgrammDateimanager.getDocument().addDocumentListener(new BeobDoc(MVConfig.SYSTEM_ORDNER_OEFFNEN, jTextFieldProgrammDateimanager));

        jTextFieldVideoplayer.setText(Daten.mVConfig.get(MVConfig.SYSTEM_PLAYER_ABSPIELEN));
        jTextFieldVideoplayer.getDocument().addDocumentListener(new BeobDoc(MVConfig.SYSTEM_PLAYER_ABSPIELEN, jTextFieldVideoplayer));

        jTextFieldProgrammUrl.setText(Daten.mVConfig.get(MVConfig.SYSTEM_URL_OEFFNEN));
        jTextFieldProgrammUrl.getDocument().addDocumentListener(new BeobDoc(MVConfig.SYSTEM_URL_OEFFNEN, jTextFieldProgrammUrl));

        jTextFieldProgrammShutdown.setText(Daten.mVConfig.get(MVConfig.SYSTEM_LINUX_SHUTDOWN));
        if (jTextFieldProgrammShutdown.getText().isEmpty()) {
            jTextFieldProgrammShutdown.setText(Konstanten.SHUTDOWN_LINUX);
            Daten.mVConfig.add(MVConfig.SYSTEM_LINUX_SHUTDOWN, Konstanten.SHUTDOWN_LINUX);
        }
        jTextFieldProgrammShutdown.getDocument().addDocumentListener(new BeobDoc(MVConfig.SYSTEM_LINUX_SHUTDOWN, jTextFieldProgrammShutdown));

        if (MVFunctionSys.getOs() != OperatingSystemType.LINUX) {
            // Funktion ist nur für Linux
            jButtonHilfeProgrammShutdown.setEnabled(false);
            jTextFieldProgrammShutdown.setEnabled(false);
            jButtonProgrammShutdown.setEnabled(false);
        }
    }

    private void init() {
        // UserAgent
        jRadioButtonAuto.setSelected(Daten.isUserAgentAuto());
        jRadioButtonManuel.setSelected(!Daten.isUserAgentAuto());

        jTextFieldUserAgent.setEditable(!Daten.isUserAgentAuto());
        jTextFieldUserAgent.setText(Daten.mVConfig.get(MVConfig.SYSTEM_USER_AGENT));
        jTextFieldAuto.setText(Konstanten.USER_AGENT_DEFAULT);

        jTextFieldProgrammDateimanager.setText(Daten.mVConfig.get(MVConfig.SYSTEM_ORDNER_OEFFNEN));
        jTextFieldProgrammUrl.setText(Daten.mVConfig.get(MVConfig.SYSTEM_URL_OEFFNEN));
    }

    private void setUserAgent() {
        if (jRadioButtonAuto.isSelected()) {
            Daten.setUserAgentAuto();
        } else {
            Daten.setUserAgentManuel(jTextFieldUserAgent.getText());
        }
        jTextFieldUserAgent.setEditable(!Daten.isUserAgentAuto());
    }

    private void setHelp() {
        jButtonHilfe.addActionListener(e -> new DialogHilfe(parentComponent, true, "\n"
                + "Dieser Text wird als User-Agent\n"
                + "an den Webserver übertragen. Das entspricht\n"
                + "der Kennung, die auch die Browser senden.").setVisible(true));
        jButtonHilfeProgrammDateimanager.addActionListener(e -> new DialogHilfe(parentComponent, true, "\n"
                + "Im Tab \"Downloads\" kann man mit der rechten\n"
                + "Maustaste den Downloadordner (Zielordner)\n"
                + "des jeweiligen Downloads öffnen.\n"
                + "Normalerweise wird der Dateimanager des\n"
                + "Betriebssystems gefunden und geöffnet. Klappt das nicht,\n"
                + "kann hier ein Programm dafür angegeben werden.").setVisible(true));
        jButtonHilfeNeuladen.addActionListener(e -> new DialogHilfe(parentComponent, true, "\n"
                + "Abos automatisch suchen:\n"
                + "Nach dem Neuladen einer Filmliste wird dann\n"
                + "sofort nach neuen Abos gesucht. Ansonsten muss man\n"
                + "im Tab Download auf \"Downloads aktualisieren\" klicken.\n"
                + "\n"
                + "Downloads sofort starten:\n"
                + "Neu angelegte Downloads (aus Abos) werden\n"
                + "sofort gestartet. Ansonsten muss man sie\n"
                + "selbst starten.\n").setVisible(true));
        jButtonHilfeVideoplayer.addActionListener(e -> new DialogHilfe(parentComponent, true, "\n"
                + "Im Tab \"Downloads\" kann man den gespeicherten\n"
                + "Film in einem Videoplayer öffnen.\n"
                + "Normalerweise wird der Videoplayer des\n"
                + "Betriebssystems gefunden und geöffnet. Klappt das nicht,\n"
                + "kann hier ein Programm dafür angegeben werden.").setVisible(true));
        jButtonHilfeProgrammUrl.addActionListener(e -> new DialogHilfe(parentComponent, true, "\n"
                + "Wenn das Programm versucht, einen Link zu öffnen\n"
                + "(z.B. den Link im Menüpunkt \"Hilfe\" zu den \"Hilfeseiten\")\n"
                + "und die Standardanwendung (z.B. \"Firefox\") nicht startet,\n"
                + "kann damit ein Programm ausgewählt und\n"
                + "fest zugeordnet werden (z.B. der Browser \"Firefox\").").setVisible(true));
        jButtonHilfeProgrammShutdown.addActionListener(e -> new DialogHilfe(parentComponent, true, "\n"
                + "Bei Linux wird das Programm/Script ausgeführt\n"
                + "um den Recher herunter zu fahren\n"
                + "\n"
                + "mögliche Aufrufe sind:\n"
                + "\n"
                + "systemctl poweroff\n"
                + "poweroff\n"
                + "sudo shutdown -P now\n"
                + "shutdown -h now").setVisible(true));
    }

    private void setIcon() {
        jButtonHilfe.setIcon(GetIcon.getProgramIcon("help_16.png"));
        jButtonHilfeNeuladen.setIcon(GetIcon.getProgramIcon("help_16.png"));
        jButtonHilfeProgrammDateimanager.setIcon(GetIcon.getProgramIcon("help_16.png"));
        jButtonHilfeVideoplayer.setIcon(GetIcon.getProgramIcon("help_16.png"));
        jButtonHilfeProgrammUrl.setIcon(GetIcon.getProgramIcon("help_16.png"));
        jButtonHilfeProgrammShutdown.setIcon(GetIcon.getProgramIcon("help_16.png"));

        jButtonProgrammDateimanager.setIcon(GetIcon.getProgramIcon("fileopen_16.png"));
        jButtonProgrammVideoplayer.setIcon(GetIcon.getProgramIcon("fileopen_16.png"));
        jButtonProgrammUrl.setIcon(GetIcon.getProgramIcon("fileopen_16.png"));
        jButtonProgrammShutdown.setIcon(GetIcon.getProgramIcon("fileopen_16.png"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        jCheckBoxAboSuchen = new javax.swing.JCheckBox();
        jCheckBoxDownloadSofortStarten = new javax.swing.JCheckBox();
        jButtonHilfeNeuladen = new javax.swing.JButton();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        jTextFieldUserAgent = new javax.swing.JTextField();
        jButtonHilfe = new javax.swing.JButton();
        jRadioButtonAuto = new javax.swing.JRadioButton();
        jRadioButtonManuel = new javax.swing.JRadioButton();
        jTextFieldAuto = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jTextFieldProgrammDateimanager = new javax.swing.JTextField();
        jButtonProgrammDateimanager = new javax.swing.JButton();
        jButtonHilfeProgrammDateimanager = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldVideoplayer = new javax.swing.JTextField();
        jButtonHilfeVideoplayer = new javax.swing.JButton();
        jButtonProgrammVideoplayer = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jTextFieldProgrammUrl = new javax.swing.JTextField();
        jButtonProgrammUrl = new javax.swing.JButton();
        jButtonHilfeProgrammUrl = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButtonHilfeProgrammShutdown = new javax.swing.JButton();
        jButtonProgrammShutdown = new javax.swing.JButton();
        jTextFieldProgrammShutdown = new javax.swing.JTextField();

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Nach dem Neuladen der Filmliste"));

        jCheckBoxAboSuchen.setText("Abos automatisch suchen");

        jCheckBoxDownloadSofortStarten.setText("Downloads aus Abos sofort starten");

        jButtonHilfeNeuladen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/help_16.png"))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxAboSuchen)
                    .addComponent(jCheckBoxDownloadSofortStarten))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonHilfeNeuladen)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxAboSuchen)
                    .addComponent(jButtonHilfeNeuladen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDownloadSofortStarten)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("User-Agent"));

        jButtonHilfe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/help_16.png"))); // NOI18N

        buttonGroup1.add(jRadioButtonAuto);
        jRadioButtonAuto.setText("Auto:");

        buttonGroup1.add(jRadioButtonManuel);

        jTextFieldAuto.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonManuel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldUserAgent))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButtonAuto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldAuto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonHilfe)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jRadioButtonAuto)
                    .addComponent(jTextFieldAuto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonHilfe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jRadioButtonManuel)
                    .addComponent(jTextFieldUserAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonHilfe, jTextFieldAuto, jTextFieldUserAgent});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tab Downloads"));

        jButtonProgrammDateimanager.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/fileopen_16.png"))); // NOI18N

        jButtonHilfeProgrammDateimanager.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/help_16.png"))); // NOI18N

        jLabel1.setText("Datei-Manager zum Öffnen des Downloadordners");

        jLabel2.setText("Videoplayer zum Abspielen gespeicherter Filme");

        jButtonHilfeVideoplayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/help_16.png"))); // NOI18N

        jButtonProgrammVideoplayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/fileopen_16.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldProgrammDateimanager)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonProgrammDateimanager)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonHilfeProgrammDateimanager))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldVideoplayer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonProgrammVideoplayer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonHilfeVideoplayer))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 180, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldProgrammDateimanager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonProgrammDateimanager)
                    .addComponent(jButtonHilfeProgrammDateimanager))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldVideoplayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonHilfeVideoplayer)
                        .addComponent(jButtonProgrammVideoplayer)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonHilfeProgrammDateimanager, jButtonProgrammDateimanager, jTextFieldProgrammDateimanager});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonHilfeVideoplayer, jButtonProgrammVideoplayer, jTextFieldVideoplayer});

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Webbrowser zum Öffnen von URLs"));

        jButtonProgrammUrl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/fileopen_16.png"))); // NOI18N

        jButtonHilfeProgrammUrl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/help_16.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldProgrammUrl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonProgrammUrl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonHilfeProgrammUrl)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldProgrammUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonProgrammUrl)
                    .addComponent(jButtonHilfeProgrammUrl))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonHilfeProgrammUrl, jButtonProgrammUrl, jTextFieldProgrammUrl});

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Linux: Aufruf zum Shutdown"));

        jButtonHilfeProgrammShutdown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/help_16.png"))); // NOI18N

        jButtonProgrammShutdown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/fileopen_16.png"))); // NOI18N

        jTextFieldProgrammShutdown.setText("shutdown -h now");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldProgrammShutdown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonProgrammShutdown)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonHilfeProgrammShutdown)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldProgrammShutdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButtonHilfeProgrammShutdown)
                        .addComponent(jButtonProgrammShutdown)))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonHilfeProgrammShutdown, jButtonProgrammShutdown, jTextFieldProgrammShutdown});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButtonHilfe;
    private javax.swing.JButton jButtonHilfeNeuladen;
    private javax.swing.JButton jButtonHilfeProgrammDateimanager;
    private javax.swing.JButton jButtonHilfeProgrammShutdown;
    private javax.swing.JButton jButtonHilfeProgrammUrl;
    private javax.swing.JButton jButtonHilfeVideoplayer;
    private javax.swing.JButton jButtonProgrammDateimanager;
    private javax.swing.JButton jButtonProgrammShutdown;
    private javax.swing.JButton jButtonProgrammUrl;
    private javax.swing.JButton jButtonProgrammVideoplayer;
    private javax.swing.JCheckBox jCheckBoxAboSuchen;
    private javax.swing.JCheckBox jCheckBoxDownloadSofortStarten;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButtonAuto;
    private javax.swing.JRadioButton jRadioButtonManuel;
    private javax.swing.JTextField jTextFieldAuto;
    private javax.swing.JTextField jTextFieldProgrammDateimanager;
    private javax.swing.JTextField jTextFieldProgrammShutdown;
    private javax.swing.JTextField jTextFieldProgrammUrl;
    private javax.swing.JTextField jTextFieldUserAgent;
    private javax.swing.JTextField jTextFieldVideoplayer;
    // End of variables declaration//GEN-END:variables

    private class BeobUserAgent implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            tus();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            tus();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            tus();
        }

        private void tus() {
            Daten.setUserAgentManuel(jTextFieldUserAgent.getText());
        }
    }

    private class BeobDoc implements DocumentListener {

        String config;
        JTextField txt;

        public BeobDoc(String config, JTextField txt) {
            this.config = config;
            this.txt = txt;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            tus();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            tus();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            tus();
        }

        private void tus() {
            Daten.mVConfig.add(config, txt.getText());
        }

    }

    private class BeobPfad implements ActionListener {

        String config;
        String title;
        JTextField textField;

        public BeobPfad(String config, String title, JTextField textField) {
            this.config = config;
            this.title = title;
            this.textField = textField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //we can use native chooser on Mac...
            if (SystemInfo.isMacOSX()) {
                FileDialog chooser = new FileDialog(daten.mediathekGui, title);
                chooser.setMode(FileDialog.LOAD);
                chooser.setVisible(true);
                if (chooser.getFile() != null) {
                    try {
                        File destination = new File(chooser.getDirectory() + chooser.getFile());
                        textField.setText(destination.getAbsolutePath());
                    } catch (Exception ex) {
                        MSLog.fehlerMeldung(915263014, ex);
                    }
                }
            } else {
                int returnVal;
                JFileChooser chooser = new JFileChooser();
                if (!textField.getText().equals("")) {
                    chooser.setCurrentDirectory(new File(textField.getText()));
                } else {
                    chooser.setCurrentDirectory(new File(GuiFunktionen.getHomePath()));
                }
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        textField.setText(chooser.getSelectedFile().getAbsolutePath());
                    } catch (Exception ex) {
                        MSLog.fehlerMeldung(751214501, ex);
                    }
                }
            }
            // merken und prüfen
            Daten.mVConfig.add(config, textField.getText());
            String programm = textField.getText();
            if (!programm.equals("")) {
                try {
                    if (!new File(programm).exists()) {
                        MVMessageDialog.showMessageDialog(daten.mediathekGui, "Das Programm:  " + "\"" + programm + "\"" + "  existiert nicht!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    } else if (!new File(programm).canExecute()) {
                        MVMessageDialog.showMessageDialog(daten.mediathekGui, "Das Programm:  " + "\"" + programm + "\"" + "  kann nicht ausgeführt werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ignored) {
                }
            }

        }

    }
}
