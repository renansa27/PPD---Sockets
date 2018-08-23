package clienteppd;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author renan
 */
public class ClientePPD {
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClienteFramePPD.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            JTextField ip = new JTextField();
            JTextField porta = new JTextField();
            Object[] dialog = {
                "Ip",ip,
                "Porta",porta
            };
            int opcao = JOptionPane.showConfirmDialog(null, dialog,"Dados para conexão",JOptionPane.CANCEL_OPTION);
            if (opcao == JOptionPane.OK_OPTION){
                ClienteFramePPD clienteFrame = new ClienteFramePPD(ip.getText(),porta.getText());
                clienteFrame.setResizable(false);
                clienteFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Cliente não criado!");
            }
            //String nome = JOptionPane.showInputDialog(null, "Escreva seu nome");
            //ClienteFramePPD clienteFrame = new ClienteFramePPD(dialog.nomeJogador,ip,porta);
            //clienteFrame.setResizable(false);
            //clienteFrame.setVisible(true);
        });
    }
}