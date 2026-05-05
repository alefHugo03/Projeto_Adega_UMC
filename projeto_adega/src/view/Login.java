
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import dao.UsuarioDAO;

import javax.swing.*;

public class Login extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnLogin;

    public Login() {
        setTitle("Login");
        setSize(300, 200);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(20, 20, 80, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(100, 20, 150, 25);
        add(txtEmail);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setBounds(20, 60, 80, 25);
        add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(100, 60, 150, 25);
        add(txtSenha);

        btnLogin = new JButton("Entrar");
        btnLogin.setBounds(100, 100, 100, 30);
        add(btnLogin);

        btnLogin.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());

        UsuarioDAO dao = new UsuarioDAO();

        if (dao.login(email, senha)) {
            JOptionPane.showMessageDialog(this, "Login OK!");
            dispose();
            new Menu(); // próxima tela
        } else {
            JOptionPane.showMessageDialog(this, "Login inválido!");
        }
    }
}