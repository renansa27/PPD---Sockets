package clienteppd;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author renan
 */

public class ClienteFramePPD extends javax.swing.JFrame {

    Socket socket;
    PrintWriter escritor;
    String nome;
    Scanner leitor;
    String mapaJogo;
    String corPeca = "vermelho";
    boolean vez = false;
    boolean desistir = false;
    String ipCliente = "";
    int portaCliente = 0;

    /**
     * Creates new form ClienteFrame
     *
     * @param ip
     * @param porta
     */
    
    public ClienteFramePPD(String ip,String porta) {
        super("Trabalho PPD - Sockets");
        ipCliente = ip;
        portaCliente = Integer.parseInt(porta);
        redeChatCliente();
        this.nome = nome;
        this.mapaJogo = "AABVV";
        initComponents();
        registraCorPeca(corPeca);
    }

    private void registraCorPeca(String cor) {
        corPeca = cor;
        if ("azul".equals(cor)) {
            vez = true;
            nome = "Jogador 1";
        }else{
            nome = "Jogador 2";
        }
        SuaCor.setText(nome+": "+cor);
    }

    public void redeChatCliente() {
        Socket socket;
        try {
            System.out.println("ip: "+ipCliente + " , porta: "+portaCliente);
            socket = new Socket(ipCliente, portaCliente);
            escritor = new PrintWriter(socket.getOutputStream());
            leitor = new Scanner(socket.getInputStream());
            new Thread(new EscutaServidor()).start();
        } catch (IOException ex) {
            Logger.getLogger(ClienteFramePPD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void atualizaTabuleiro(String mapaJogoRecebido) {
        char[] posicoesRecebidas = mapaJogoRecebido.toCharArray();
        mapaJogo = mapaJogoRecebido;

        for (int i = 0; i < 5; i++) {
            switch (posicoesRecebidas[i]) {
                case 'A':
                    switch (i) {
                        case 0:
                            button1.setBackground(new java.awt.Color(51, 51, 240));
                            break;
                        case 1:
                            button2.setBackground(new java.awt.Color(51, 51, 240));
                            break;
                        case 2:
                            button3.setBackground(new java.awt.Color(51, 51, 240));
                            break;
                        case 3:
                            button4.setBackground(new java.awt.Color(51, 51, 240));
                            break;
                        default:
                            button5.setBackground(new java.awt.Color(51, 51, 240));
                            break;
                    }
                    break;
                case 'V':
                    switch (i) {
                        case 0:
                            button1.setBackground(new java.awt.Color(255, 0, 0));
                            break;
                        case 1:
                            button2.setBackground(new java.awt.Color(255, 0, 0));
                            break;
                        case 2:
                            button3.setBackground(new java.awt.Color(255, 0, 0));
                            break;
                        case 3:
                            button4.setBackground(new java.awt.Color(255, 0, 0));
                            break;
                        default:
                            button5.setBackground(new java.awt.Color(255, 0, 0));
                            break;
                    }
                    break;
                case 'B':
                    switch (i) {
                        case 0:
                            button1.setBackground(new java.awt.Color(255, 255, 255));
                            break;
                        case 1:
                            button2.setBackground(new java.awt.Color(255, 255, 255));
                            break;
                        case 2:
                            button3.setBackground(new java.awt.Color(255, 255, 255));
                            break;
                        case 3:
                            button4.setBackground(new java.awt.Color(255, 255, 255));
                            break;
                        default:
                            button5.setBackground(new java.awt.Color(255, 255, 255));
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void verificarGanhador(String mapaJogo) {
        if(vez){
            if ("vermelho".equals(corPeca) && ("BVAAV".equals(mapaJogo) || "VBAVA".equals(mapaJogo))) {
                //desistir = true;
                escritor.println("ven;azul");
                escritor.flush();
                desistir = true;
                reiniciaJogo();
            } else if ("azul".equals(corPeca) && ("ABVAV".equals(mapaJogo) || "BAVVA".equals(mapaJogo))) {
                escritor.println("ven;vermelho");
                escritor.flush();
                reiniciaJogo();
            }
        }
    }

    private void reiniciaJogo() {
        mapaJogo = "AABVV";
        if ("azul".equals(corPeca)) {
            vez = true;
        }else{
            vez=false;
        }
        desistir = false;
        atualizaTabuleiro(mapaJogo);
    }

    private class EscutaServidor implements Runnable {

        @Override
        public void run() {
            String textoRecebido;
            while ((textoRecebido = leitor.nextLine()) != null) {
                String array[];
                array = textoRecebido.split(";");
                String verificador = array[0];
                switch (verificador) {
                    case ("msg"):
                        AreaTextoReceber.append(textoRecebido.substring(4) + "\n");
                        break;
                        //msg;Oi
                    case ("mov"):
                        atualizaTabuleiro(array[1]);
                        vez = !vez;
                        verificarGanhador(array[1]);
                        break;

                    case ("cor"):
                        //System.out.println(array[1]);
                        registraCorPeca(array[1]);
                        break;

                    case ("des"):
                        if (desistir == false) {
                            JOptionPane.showMessageDialog(null, "Parabéns " + nome + ", você venceu!");
                        }
                        reiniciaJogo();
                        break;
                    case ("con"):
                        JOptionPane.showMessageDialog(null, "Seu oponente abandonou a partida, você venceu!");
                        reiniciaJogo();
                        break;
                        
                    case ("ven"):
                        if("azul".equals(array[1]) && "azul".equals(corPeca)){
                            JOptionPane.showMessageDialog(null, "Parabéns " + nome + ", você venceu!");
                            reiniciaJogo();
                        }else if("vermelho".equals(array[1]) && "vermelho".equals(corPeca)){
                            JOptionPane.showMessageDialog(null, "Parabéns " + nome + ", você venceu!");
                            reiniciaJogo();
                        }else{
                            JOptionPane.showMessageDialog(null, nome + ", você perdeu!");
                            reiniciaJogo();
                        }
                        break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        PanelChat = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        AreaTextoEnviar = new javax.swing.JTextArea();
        Desistir = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        AreaTextoReceber = new javax.swing.JTextArea();
        PanelJogo = new javax.swing.JPanel();
        button1 = new java.awt.Button();
        button2 = new java.awt.Button();
        button4 = new java.awt.Button();
        button5 = new java.awt.Button();
        button3 = new java.awt.Button();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        SuaCor = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                Fechar(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PONG-HAU-KI", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 437, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        PanelChat.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chat", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        AreaTextoEnviar.setColumns(20);
        AreaTextoEnviar.setLineWrap(true);
        AreaTextoEnviar.setRows(1);
        AreaTextoEnviar.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        AreaTextoEnviar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AreaTextoEnviarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ResolveEnter(evt);
            }
        });
        jScrollPane1.setViewportView(AreaTextoEnviar);

        Desistir.setText("Desistir");
        Desistir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DesistirMouseClicked(evt);
            }
        });

        AreaTextoReceber.setEditable(false);
        AreaTextoReceber.setColumns(20);
        AreaTextoReceber.setLineWrap(true);
        AreaTextoReceber.setRows(5);
        AreaTextoReceber.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        AreaTextoReceber.setDragEnabled(true);
        jScrollPane3.setViewportView(AreaTextoReceber);

        javax.swing.GroupLayout PanelChatLayout = new javax.swing.GroupLayout(PanelChat);
        PanelChat.setLayout(PanelChatLayout);
        PanelChatLayout.setHorizontalGroup(
            PanelChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelChatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelChatLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Desistir, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66))
        );
        PanelChatLayout.setVerticalGroup(
            PanelChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelChatLayout.createSequentialGroup()
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Desistir)
                .addContainerGap())
        );

        PanelJogo.setBackground(new java.awt.Color(255, 255, 255));
        PanelJogo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelJogo.setForeground(new java.awt.Color(240, 240, 240));

        button1.setBackground(new java.awt.Color(51, 51, 240));
        button1.setForeground(new java.awt.Color(51, 51, 255));
        button1.setBackground(new java.awt.Color(51,51,255));
        button1.setLabel("");
        button1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button1MouseClicked(evt);
            }
        });

        button2.setBackground(new java.awt.Color(51, 51, 240));
        button2.setForeground(new java.awt.Color(51, 51, 255));
        button2.setLabel("");
        button2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button2MouseClicked(evt);
            }
        });

        button4.setBackground(new java.awt.Color(255, 0, 0));
        button4.setForeground(new java.awt.Color(51, 51, 255));
        button4.setLabel("");
        button4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button4MouseClicked(evt);
            }
        });

        button5.setBackground(new java.awt.Color(255, 0, 0));
        button5.setForeground(new java.awt.Color(51, 51, 255));
        button5.setLabel("");
        button5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button5MouseClicked(evt);
            }
        });

        button3.setBackground(new java.awt.Color(255, 255, 255));
        button3.setForeground(new java.awt.Color(255, 255, 255));
        button3.setLabel("");
        button3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                button3MouseClicked(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/horizontal.png"))); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/vertical.png"))); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/diagonal_2.png"))); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/Diagonal3.png"))); // NOI18N

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/Diagonal1.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/Diagonal4.png"))); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs/vertical.png"))); // NOI18N

        javax.swing.GroupLayout PanelJogoLayout = new javax.swing.GroupLayout(PanelJogo);
        PanelJogo.setLayout(PanelJogoLayout);
        PanelJogoLayout.setHorizontalGroup(
            PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelJogoLayout.createSequentialGroup()
                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelJogoLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelJogoLayout.createSequentialGroup()
                        .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelJogoLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel4)
                                .addGap(29, 29, 29))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelJogoLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(PanelJogoLayout.createSequentialGroup()
                                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6))
                                .addGap(59, 59, 59)
                                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(PanelJogoLayout.createSequentialGroup()
                                .addGap(158, 158, 158)
                                .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelJogoLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(14, 14, 14)))))
                .addContainerGap(55, Short.MAX_VALUE))
            .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelJogoLayout.createSequentialGroup()
                    .addGap(0, 243, Short.MAX_VALUE)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 243, Short.MAX_VALUE)))
        );
        PanelJogoLayout.setVerticalGroup(
            PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelJogoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelJogoLayout.createSequentialGroup()
                        .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(PanelJogoLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(PanelJogoLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jLabel6)))
                        .addGap(29, 29, 29)
                        .addComponent(jLabel3)
                        .addGap(403, 403, 403))
                    .addGroup(PanelJogoLayout.createSequentialGroup()
                        .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelJogoLayout.createSequentialGroup()
                                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(23, 23, 23)
                                .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(button4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel8))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(PanelJogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelJogoLayout.createSequentialGroup()
                    .addGap(0, 407, Short.MAX_VALUE)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 408, Short.MAX_VALUE)))
        );

        SuaCor.setText("Sua cor:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(PanelJogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SuaCor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(PanelChat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SuaCor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelJogo, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PanelChat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AreaTextoEnviarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AreaTextoEnviarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            escritor.println("msg;" + this.nome + " : " + AreaTextoEnviar.getText());
            escritor.flush();
            AreaTextoEnviar.requestFocus();
        }
    }//GEN-LAST:event_AreaTextoEnviarKeyPressed

    private void DesistirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DesistirMouseClicked
        int respostaDesistir = JOptionPane.showConfirmDialog(null, "Aceitando a derrota uma nova partida será criada!", "Reiniar jogo", JOptionPane.YES_NO_OPTION);
        if (respostaDesistir == JOptionPane.YES_OPTION) {
            desistir = true;
            try{
                escritor.println("des;");
                escritor.flush();
            }catch(Exception e){
                
            }
            JOptionPane.showMessageDialog(null, nome + ", Você perdeu!");
        }
    }//GEN-LAST:event_DesistirMouseClicked

    private void Fechar(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_Fechar
        int derrota = JOptionPane.showConfirmDialog(null, "Aceita a derrota?", "Fechar jogo", JOptionPane.YES_NO_OPTION);
        if(derrota == JOptionPane.YES_OPTION){
            try{
                escritor.println("con;");
                escritor.flush();
            }catch(Exception e){
                
            }
            System.exit(0);
        }
    }//GEN-LAST:event_Fechar

    private void button3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button3MouseClicked
        if (vez) {
            char[] posicoes = mapaJogo.toCharArray();
            Color cor = button3.getBackground();
            //System.out.println("Cor do botão é: " + cor.getRGB());
            if (((cor.getRGB() == -13421584 || cor.getRGB() == -13421569) && "azul".equals(corPeca)) || (cor.getRGB() == -65536 && "vermelho".equals(corPeca))) {
                if (posicoes[0] == 'B') {
                    button1.setBackground(new java.awt.Color(cor.getRGB()));
                    button3.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 0, 2);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[1] == 'B') {
                    button2.setBackground(new java.awt.Color(cor.getRGB()));
                    button3.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 2, 1);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[3] == 'B') {
                    button4.setBackground(new java.awt.Color(cor.getRGB()));
                    button3.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 3, 2);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[4] == 'B') {
                    button5.setBackground(new java.awt.Color(cor.getRGB()));
                    button3.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 2, 4);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();
                } else {
                    JOptionPane.showMessageDialog(null, "Jogada proibida!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Mova apenas sua peça!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Espere sua vez!");
        }
    }//GEN-LAST:event_button3MouseClicked

    private void button5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button5MouseClicked
        if (vez) {
            char[] posicoes = mapaJogo.toCharArray();
            Color cor = button5.getBackground();
            //System.out.println("Cor do botão é: " + cor.getRGB());
            if (((cor.getRGB() == -13421584 || cor.getRGB() == -13421569) && "azul".equals(corPeca)) || (cor.getRGB() == -65536 && "vermelho".equals(corPeca))) {
                if (posicoes[1] == 'B') {
                    button2.setBackground(new java.awt.Color(cor.getRGB()));
                    button5.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 4, 1);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[2] == 'B') {
                    button3.setBackground(new java.awt.Color(cor.getRGB()));
                    button5.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 4, 2);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[3] == 'B') {
                    button4.setBackground(new java.awt.Color(cor.getRGB()));
                    button5.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 3, 4);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();
                } else {
                    JOptionPane.showMessageDialog(null, "Jogada proibida!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Mova apenas sua peça!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Espere sua vez!");
        }
    }//GEN-LAST:event_button5MouseClicked

    private void button4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button4MouseClicked
        if (vez) {
            char[] posicoes = mapaJogo.toCharArray();
            Color cor = button4.getBackground();
            //System.out.println("Cor do botão é: " + cor.getRGB());
            if (((cor.getRGB() == -13421584 || cor.getRGB() == -13421569) && "azul".equals(corPeca)) || (cor.getRGB() == -65536 && "vermelho".equals(corPeca))) {
                if (posicoes[0] == 'B') {
                    button1.setBackground(new java.awt.Color(cor.getRGB()));
                    button4.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 0, 3);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[2] == 'B') {
                    button3.setBackground(new java.awt.Color(cor.getRGB()));
                    button4.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 2, 3);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[4] == 'B') {
                    button5.setBackground(new java.awt.Color(cor.getRGB()));
                    button4.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 3, 4);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();
                } else {
                    JOptionPane.showMessageDialog(null, "Jogada proibida!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Mova apenas sua peça!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Espere sua vez!");
        }
    }//GEN-LAST:event_button4MouseClicked

//Clique do Mouse

    private void button2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button2MouseClicked
        if (vez) {
            char[] posicoes = mapaJogo.toCharArray();
            Color cor = button2.getBackground();
            //System.out.println("Cor do botão é: " + cor.getRGB());
            if (((cor.getRGB() == -13421584 || cor.getRGB() == -13421569) && "azul".equals(corPeca)) || (cor.getRGB() == -65536 && "vermelho".equals(corPeca))) {
                if (posicoes[2] == 'B') {
                    button3.setBackground(new java.awt.Color(cor.getRGB()));
                    button2.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();
                    swap(posicoes, 1, 2);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[4] == 'B') {
                    button5.setBackground(new java.awt.Color(cor.getRGB()));
                    button2.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();
                    swap(posicoes, 1, 4);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();
                } else {
                    JOptionPane.showMessageDialog(null, "Jogada proibida!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Mova apenas sua peça!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Espere sua vez!");
        }
    }//GEN-LAST:event_button2MouseClicked

    private void button1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_button1MouseClicked
        if (vez) {
            char[] posicoes = mapaJogo.toCharArray();
            Color cor = button1.getBackground();
            //System.out.println("Cor do botão é: " + cor.getRGB());
            if (((cor.getRGB() == -13421584 || cor.getRGB() == -13421569) && "azul".equals(corPeca)) || (cor.getRGB() == -65536 && "vermelho".equals(corPeca))) {
                if (posicoes[2] == 'B') {
                    button3.setBackground(new java.awt.Color(cor.getRGB()));
                    button1.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 0, 2);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();

                } else if (posicoes[3] == 'B') {
                    button4.setBackground(new java.awt.Color(cor.getRGB()));
                    button1.setBackground(new java.awt.Color(255, 255, 255));
                    repaint();

                    swap(posicoes, 0, 3);
                    mapaJogo = String.valueOf(posicoes);
                    escritor.println("mov;" + mapaJogo);
                    escritor.flush();
                } else {
                    JOptionPane.showMessageDialog(null, "Jogada proibida!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Mova apenas sua peça!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Espere sua vez!");
        }
    }//GEN-LAST:event_button1MouseClicked

    private void ResolveEnter(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ResolveEnter
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            AreaTextoEnviar.setText("");
        }
    }//GEN-LAST:event_ResolveEnter

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea AreaTextoEnviar;
    private javax.swing.JTextArea AreaTextoReceber;
    private javax.swing.JButton Desistir;
    private javax.swing.JPanel PanelChat;
    private javax.swing.JPanel PanelJogo;
    private javax.swing.JLabel SuaCor;
    private java.awt.Button button1;
    private java.awt.Button button2;
    private java.awt.Button button3;
    private java.awt.Button button4;
    private java.awt.Button button5;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

    private void swap(char[] posicoes, int num1, int num2) {
        char aux = posicoes[num2];
        posicoes[num2] = posicoes[num1];
        posicoes[num1] = aux;
    }
}
