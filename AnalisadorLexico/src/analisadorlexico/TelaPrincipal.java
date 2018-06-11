/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorlexico;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import glazed.IdTableFormat;
import glazed.TokenTableFormat;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import other.FiltroDeArquivos;
import other.UtilidadesArquivos;

/**
 *
 * @author FELLIPE PRATES \\ LUIZ SERGIO \\ TIAGO ELIAS DATA = 17-03-2018
 */
public class TelaPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form TelaPrincipal
     */
    File arquivo = null;
    String conteudo = "";
    String linguagem = "";

    int tamanhoTabela = 68;
    int numerosLinhas = 0;
    private EventList<Token> tokens = new BasicEventList<>();
    private EventList<Identificador> identificadores = new BasicEventList<>();
    Token tk = new Token();
    Identificador ident = new Identificador();

    String simbolos = "+-*/=^<>()[]{}.,:;'#$"; //simbolos que devem ser reconhecidos
    char[] vetSimbolos = simbolos.toCharArray(); // vetor de char para acessar por char e comparar por posicao 

    //TODAS AS PALAVRAS RESERVADAS
    
    String TableReservada[]
            = {"and", "downto", "in", "packed", "to", "array", "else", "inline", "procedure", "type", "asm", "end", "interface",
                "program", "unit", "begin", "file", "label", "record", "until", "case", "for", "mod", "repeat", "until", "const",
                "foward", "nil", "set", "uses", "constructor", "function", "not", "shl", "var", "destructor", "goto", "object",
                "shr", "while", "div", "if", "of", "string", "with", "do", "implementation", "or", "then", "xor", "shotint", "integer", "longint", "byte",
                "word", "real", "single", "double", "extended", "comp", "string", "char", "boolean", "end.", "writeln", "readln", "read", "write"};

    //"char", "enumerado", "subintervalo", "string", "array", "record", "set", "file", "text", "pointer", "then", "begin", "functio", "do", "while", "longint
    public boolean pegarSimbolos(String palavra) {
        boolean valida = false;
        for (char c : vetSimbolos) {
            if (palavra.equals(c + "")) {
                valida = true;
                break;
            }
        }
        return valida;
    }

    public boolean palavraReservada(String palavra) {
        boolean validar = false;
        for (String s : TableReservada) {
            if (s.equals(palavra)) {
                validar = true;
                break;
            }
        }
        return validar;
    }

    public Token RotinaNumeros(Token token) {

        String lexema = "";
        int i = 0;
        int tamanho = token.getLexema().length();
        //Token tk = new Token();

        while (token.getLexema().charAt(i) >= 48 && token.getLexema().charAt(i) <= 57) {
            lexema = lexema + token.getLexema().charAt(i);

            if (i != tamanho - 1) {
                i++;
            } else {
                break;
            }
        }
        token.setClasse("cInt");

        if (token.getLexema().charAt(i) == 46) { //se for ponto (.)
            lexema = lexema + token.getLexema().charAt(i);
            i++;

            if ((token.getLexema().charAt(i) >= 48) && (token.getLexema().charAt(i) <= 57) && i < tamanho) {

                while ((token.getLexema().charAt(i) >= 48) && (token.getLexema().charAt(i) <= 57)) {
                    lexema = lexema + token.getLexema().charAt(i);

                    if (i != tamanho - 1) {
                        i++;
                    } else {
                        break;
                    }
                }
                token.setClasse("cReal");
            } else {
                jTextAreaSaida.setText("Erro Lexico - Caractere + Caractere + Desconhecido");
            }
        }

        return token;
    }

    public Token RotinaIdentificador(Token token) {
        String lexema = "";
        int tamanho = token.getLexema().length();
        int i = 0;
        //Token tk = new Token();

        while (((token.getLexema().charAt(i) >= 48) && (token.getLexema().charAt(i) <= 57)) //numeros
                || ((token.getLexema().charAt(i) >= 65) && (token.getLexema().charAt(i) <= 90)) //alfabeto maiusculo
                || ((token.getLexema().charAt(i) >= 97) && (token.getLexema().charAt(i) <= 122))) { //alfabeto minusculo

            lexema = lexema + token.getLexema().charAt(i);

            //condiçao de parada
            if (i != tamanho - 1) {
                i++;
            } else {
                break;
            }
        }

        boolean reservada = palavraReservada(token.getLexema());
        boolean simbolo = getSpecialCharacterCount(token.getLexema(), true);

        //System.out.println("teste 4 " + reservada + simbolo);
        if (reservada) {
            token.setClasse("Palavra Reservada");
        } else if (simbolo) {

            String ce = token.getLexema();

            if (ce.charAt(0) == '"') {
                token.setClasse("cStr");
            }
            if (ce.charAt(0) == '´') {
                token.setClasse("cStr");
            }
            if (ce.charAt(0) == '\'') {
                token.setClasse("cStr");
            }
            if (":".equals(ce)) {
                token.setClasse("2 pontos");
            }
            if ("{".equals(ce)) {
                token.setClasse("ChA");
            }
            if ("}".equals(ce)) {
                token.setClasse("ChF");
            }
            if (",".equals(ce)) {
                token.setClasse("cVir");
            }
            if ("=".equals(ce)) {
                token.setClasse("cEQ");
            }
            if ("(".equals(ce)) {
                token.setClasse("cLPar");
            }
            if (")".equals(ce)) {
                token.setClasse("cDPar");
            }
            if ("+".equals(ce)) {
                token.setClasse("cAdd");
            }
            if ("-".equals(ce)) {
                token.setClasse("cSub");
            }
            if ("/".equals(ce)) {
                token.setClasse("cDiv");
            }
            if ("*".equals(ce)) {
                token.setClasse("cMul");
            }
            if ("<".equals(ce)) {
                token.setClasse("cLT");
            }
            if (">".equals(ce)) {
                token.setClasse("cGT");
            }
            if (";".equals(ce)) {
                token.setClasse("cPVir");
            }
            if (">=".equals(ce)) {
                token.setClasse("cGE");
            }
            if ("<=".equals(ce)) {
                token.setClasse("cLE");
            }
            if ("<>".equals(ce)) {
                token.setClasse("cNE");
            }
            if (":=".equals(ce)) {
                token.setClasse("cAtr");
            }
            //token.setClasse("Caractere Especial");
        } else {
            token.setClasse("cId");
        }

        //token.setLexema(lexema);
        return token;
    }

    public boolean getSpecialCharacterCount(String s, boolean validaReal) {
        if (s == null || s.trim().isEmpty()) {
            System.out.println("Incorrect format of string");
            return false;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9._áéíóúâêîôûç]"); // aceita ponto
        Pattern p2 = Pattern.compile("[^A-Za-z0-9]"); // não aceita ponto

        if (validaReal) {
            Matcher m = p.matcher(s);
            boolean b = m.find();
            if (b == true) {
                return true;
            } else {
                return false;
            }
        } else {
            Matcher m = p2.matcher(s);
            boolean b = m.find();
            if (b == true) {
                return true;
            } else {
                return false;
            }
        }

    }

    public TelaPrincipal() {
        System.out.println("TESTE GUIT LUIZ");
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaSaida = new javax.swing.JTextArea();
        jTabbedPaneEdicao = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabelExecutar = new javax.swing.JLabel();
        jLabelCompilar = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuArquivo = new javax.swing.JMenu();
        jMenuItemNovo = new javax.swing.JMenuItem();
        jMenuItemAbrir = new javax.swing.JMenuItem();
        jMenuItemSalvar = new javax.swing.JMenuItem();
        jMenuItemSalvarComo = new javax.swing.JMenuItem();
        jMenuItemSair = new javax.swing.JMenuItem();
        jMenuEditar = new javax.swing.JMenu();
        jMenuItemRecortar = new javax.swing.JMenuItem();
        jMenuItemCopiar = new javax.swing.JMenuItem();
        jMenuItemColar = new javax.swing.JMenuItem();
        jMenuProjeto = new javax.swing.JMenu();
        jMenuItemCompilar = new javax.swing.JMenuItem();
        jMenuSobre = new javax.swing.JMenu();
        jMenuItemProjeto = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("COMPILADOR");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Resultado / (SAIDA)"));

        jTextAreaSaida.setColumns(20);
        jTextAreaSaida.setRows(5);
        jScrollPane2.setViewportView(jTextAreaSaida);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextArea.setBackground(new java.awt.Color(255, 255, 204));
        jTextArea.setColumns(20);
        jTextArea.setRows(5);
        jScrollPane1.setViewportView(jTextArea);

        jTabbedPaneEdicao.addTab("Fonte", jScrollPane1);

        jTable1.setModel(GlazedListsSwing.eventTableModel(tokens, new TokenTableFormat()));
        jTable1.setRequestFocusEnabled(false);
        jTable1.setRowHeight(20);
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 770, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneEdicao.addTab("Itens Lexicos", jPanel3);

        jTable2.setModel(GlazedListsSwing.eventTableModel(identificadores, new IdTableFormat()));
        jScrollPane4.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(449, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jTabbedPaneEdicao.addTab("Tabela de Símbolos", jPanel5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPaneEdicao)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPaneEdicao, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPaneEdicao.getAccessibleContext().setAccessibleName("Edição");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabelExecutar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/btnPlay.png"))); // NOI18N
        jLabelExecutar.setToolTipText("Executar");
        jLabelExecutar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabelExecutarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelExecutarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelExecutarMousePressed(evt);
            }
        });

        jLabelCompilar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/compilar.png"))); // NOI18N
        jLabelCompilar.setToolTipText("Compilar");
        jLabelCompilar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabelCompilarMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelCompilarMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelCompilar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelExecutar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelCompilar)
                    .addComponent(jLabelExecutar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenuArquivo.setText("Arquivo");

        jMenuItemNovo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNovo.setText("Novo");
        jMenuItemNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNovoActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemNovo);

        jMenuItemAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemAbrir.setText("Abrir");
        jMenuItemAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAbrirActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemAbrir);

        jMenuItemSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSalvar.setText("Salvar");
        jMenuItemSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalvarActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemSalvar);

        jMenuItemSalvarComo.setText("Salvar Como");
        jMenuItemSalvarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalvarComoActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemSalvarComo);

        jMenuItemSair.setText("Sair");
        jMenuItemSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSairActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jMenuItemSair);

        jMenuBar1.add(jMenuArquivo);

        jMenuEditar.setText("Editar");

        jMenuItemRecortar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemRecortar.setText("Recortar");
        jMenuEditar.add(jMenuItemRecortar);

        jMenuItemCopiar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopiar.setText("Copiar");
        jMenuEditar.add(jMenuItemCopiar);

        jMenuItemColar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemColar.setText("Colar");
        jMenuEditar.add(jMenuItemColar);

        jMenuBar1.add(jMenuEditar);

        jMenuProjeto.setText("Projeto");

        jMenuItemCompilar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        jMenuItemCompilar.setText("Compilar");
        jMenuItemCompilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCompilarActionPerformed(evt);
            }
        });
        jMenuProjeto.add(jMenuItemCompilar);

        jMenuBar1.add(jMenuProjeto);

        jMenuSobre.setText("Sobre");

        jMenuItemProjeto.setText("Projeto");
        jMenuItemProjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProjetoActionPerformed(evt);
            }
        });
        jMenuSobre.add(jMenuItemProjeto);

        jMenuBar1.add(jMenuSobre);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAbrirActionPerformed
        // TODO add your handling code here:
        JFileChooser jfc = new JFileChooser(UtilidadesArquivos.getDiretorioDoPrograma());
        jfc.setFileFilter(new FiltroDeArquivos());

        String extensao, tipo, valores[];

        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            arquivo = jfc.getSelectedFile();
            extensao = UtilidadesArquivos.getExtensaoArquivo(arquivo);
            tipo = UtilidadesArquivos.getNomeArquivo(arquivo).contains("int") ? "int" : "string";

            if (extensao.equals("txt")) {  // Texto
                conteudo = UtilidadesArquivos.lerArquivoTexto(arquivo);
                //linguagem=conteudo;
                //valores = conteudo.split(UtilidadesArquivos.getCaractereNovaLinha());
                jTextArea.setText(conteudo);
            }
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        }
        if (conteudo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Arquivo não contém nada");
        }
        System.out.println(conteudo);


    }//GEN-LAST:event_jMenuItemAbrirActionPerformed

    private void jMenuItemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalvarActionPerformed
        // TODO add your handling code here:
        String novoConteudo = jTextArea.getText();
        if (arquivo == null) {
            jMenuItemSalvarComoActionPerformed(evt);
        } else {
            UtilidadesArquivos.salvarEmArquivoTexto(novoConteudo, arquivo);
        }

    }//GEN-LAST:event_jMenuItemSalvarActionPerformed

    private void jMenuItemSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSairActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItemSairActionPerformed

    private void jMenuItemProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProjetoActionPerformed
        // TODO add your handling code here:
        System.out.println("PROJETO IFTM 9 PERIODO");
        JOptionPane.showMessageDialog(rootPane, "PROJETO COMPILADORES\n"
                + "IFTM / ENGENHARIA DA COMPUTAÇÃO\n"
                + "======================================\n"
                + "FELLIPE PRATES - LUIZ SERGIO - TIAGO ELIAS");
    }//GEN-LAST:event_jMenuItemProjetoActionPerformed

    private void jMenuItemSalvarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalvarComoActionPerformed
        // TODO add your handling code here:
        int opc = 0;
        JFileChooser j = new JFileChooser(UtilidadesArquivos.getDiretorioDoPrograma());

        j.setFileSelectionMode(JFileChooser.FILES_ONLY);
        opc = j.showSaveDialog(this);

        System.out.println(opc);

        if (opc == 0) {
            arquivo = j.getSelectedFile();
            UtilidadesArquivos.salvarEmArquivoTexto(jTextArea.getText(), arquivo);
        }

    }//GEN-LAST:event_jMenuItemSalvarComoActionPerformed

    private void jMenuItemNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNovoActionPerformed
        // TODO add your handling code here:
        jTextArea.setText("");
        arquivo = null;
        conteudo = "";
    }//GEN-LAST:event_jMenuItemNovoActionPerformed

    public void AnaliseLexica() {
        tokens.clear();
        identificadores.clear();
        jTextAreaSaida.setText("");
        String codigo = jTextArea.getText();
        numerosLinhas = 0;
        tk = null;

        if (!codigo.isEmpty()) {

            int tamanho = codigo.length(); //tamanho do codigo
            String linhas[] = codigo.split("\n"); //divindo a string por linhas
            numerosLinhas = linhas.length; //numero de linhas
            boolean valida = true;
            boolean validaReal = true;
            int i = 0;
            int j = 0;
            int tam = 0;
            String lexema = "";

            for (i = 0; i < numerosLinhas; i++) {
                tam = linhas[i].length(); //tamanho da linha
                try {
                    for (j = 0; j < tam; j++) {

                        if (linhas[i].charAt(j) > 32) { //verificando se o caractere eh diferente de um espaço ou tabulaçao

                            //para eliminar os comentarios
                            if (linhas[i].charAt(j) == '{') {
                                while (linhas[i].charAt(j) != '}') {
                                    j++;
                                }
                            } //para cadeia de string
                            else if (linhas[i].charAt(j) == '"' || linhas[i].charAt(j) == '\'' || linhas[i].charAt(j) == '´') {
                                int cadeia = 0;
                                while (cadeia != 2) {

                                    lexema = lexema + linhas[i].charAt(j);

                                    if (linhas[i].charAt(j) == '"' || linhas[i].charAt(j) == '\'' || linhas[i].charAt(j) == '´') {
                                        cadeia++;
                                    }
                                    j++;
                                }
                                j--;
                                if (!"".equals(lexema)) {
                                    tk = new Token();
                                    tk.setLexema(lexema);
                                    tk.setLinha(i + 1);
                                    tk.setColuna(j - lexema.length() + 1);
                                    tokens.add(tk);
                                    lexema = "";
                                }

                            } // para verificar as combinaçoes <> >= <= :=
                            else if ((linhas[i].charAt(j) == ':' && linhas[i].charAt(j + 1) == '=' && j + 1 != tam)
                                    || (linhas[i].charAt(j) == '>' && linhas[i].charAt(j + 1) == '=' && j + 1 != tam)
                                    || (linhas[i].charAt(j) == '<' && linhas[i].charAt(j + 1) == '=' && j + 1 != tam)
                                    || (linhas[i].charAt(j) == '<' && linhas[i].charAt(j + 1) == '>') && j + 1 != tam) {

                                lexema = lexema + linhas[i].charAt(j) + linhas[i].charAt(j + 1);
                                j++;
                                if (!"".equals(lexema)) {
                                    tk = new Token();
                                    tk.setLexema(lexema);
                                    tk.setLinha(i + 1);
                                    tk.setColuna(j + 1);
                                    tokens.add(tk);
                                    lexema = "";
                                }
                            } else if (getSpecialCharacterCount((linhas[i].charAt(j) + ""), validaReal)) {
                                if (!"".equals(lexema)) {
                                    tk = new Token();
                                    tk.setLexema(lexema);
                                    tk.setLinha(i + 1);
                                    tk.setColuna(j - lexema.length() + 1);
                                    tokens.add(tk);
                                    lexema = "";
                                }
                                tk = new Token();
                                tk.setLexema(linhas[i].charAt(j) + "");
                                tk.setLinha(i + 1);
                                tk.setColuna(j + 1);
                                tokens.add(tk);
                                validaReal = true; // reiniciando
                            } else {
                                if (linhas[i].charAt(j) == 46) { // ponto, se ja ocrrer 1 x ponto não deixa passar mais ponto
                                    validaReal = false;
                                }
                                lexema = lexema + linhas[i].charAt(j);
                            }
                        } else {

                            if (!"".equals(lexema)) {
                                tk = new Token();
                                tk.setLexema(lexema);
                                tk.setLinha(i + 1);
                                tk.setColuna(j - lexema.length() + 1);
                                tokens.add(tk);
                                lexema = "";
                            }
                            lexema = "";

                        }
                    }

                } catch (Exception e) {
                    jTextAreaSaida.append("ERRO NA SINTAXE DO CODIGO! - LINHA " + (i + 1) + "\n");
                }
                if (!"".equals(lexema)) {
                    tk = new Token();
                    tk.setLexema(lexema);
                    tk.setLinha(i + 1);
                    tk.setColuna(j - lexema.length() + 1);
                    tokens.add(tk);
                    lexema = "";
                }

            }

            for (Token token : tokens) {

                token.setLexema(token.getLexema().toLowerCase());

                if (token.getLexema().charAt(0) >= 48 && token.getLexema().charAt(0) <= 57) {
                    token = RotinaNumeros(token);
                    //tokens.get(i).setClasse(token.getClasse());
                    token.setClasse(token.getClasse());
                } else {
                    RotinaIdentificador(token);
                    token.setClasse(token.getClasse());
                    //tokens.get(i).setClasse(token.getClasse());
                }
            }

            tk = new Token();
            tk.setLexema("Eof");
            tk.setClasse("cEof");
            tk.setLinha(i);
            tk.setColuna(j + 1);
            tokens.add(tk);

            //System.out.println("CHEGOU AQUI");
            System.out.println("###TODOS TOKENS###\n" + tokens.toString());
            jTextAreaSaida.append("LEXEMAS ENCONTRADOS!");

            //IDENTIFICADORES
            int endereco = 0;
            boolean verifica=false; 
            List<Identificador> identAux;
            
            for (Token token : tokens) {
                if (token.getClasse().equals("cId")) {
                    
                    ident = new Identificador();
                    ident.setLexema(token.getLexema());
                    ident.setClasse(token.getClasse());
                    ident.setNivel(0);
                    ident.setEndereco(endereco);
                    System.out.println("teste ----- " + ident.toString());
                    
                    for (Identificador aux : identificadores) {
                        verifica=aux.getLexema().equals(ident.getLexema());  
                        if(verifica){
                            break;
                        }
                    }
                    
                    if(!verifica){
                        identificadores.add(ident);
                    }
                    verifica=false;
                    endereco++;
                }
            }

        } else {
            String saida = jTextAreaSaida.getText();
            saida = saida + "ERRO - Digite o Código;\n";
            jTextAreaSaida.setText(saida);
        }
    }


    private void jMenuItemCompilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCompilarActionPerformed
        //A PARTE DIFICIL
        AnaliseLexica();
    }//GEN-LAST:event_jMenuItemCompilarActionPerformed

    private void jLabelExecutarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelExecutarMouseEntered
        // TODO add your handling code here:

    }//GEN-LAST:event_jLabelExecutarMouseEntered

    private void jLabelExecutarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelExecutarMouseExited
        // TODO add your handling code here:
        ImageIcon II = new ImageIcon(getClass().getResource("/imagens/btnPlay.png"));
        jLabelExecutar.setIcon(II);
    }//GEN-LAST:event_jLabelExecutarMouseExited

    private void jLabelExecutarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelExecutarMousePressed
        // TODO add your handling code here:
        ImageIcon II = new ImageIcon(getClass().getResource("/imagens/btnPlayPress.png"));
        jLabelExecutar.setIcon(II);

    }//GEN-LAST:event_jLabelExecutarMousePressed

    private void jLabelCompilarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCompilarMousePressed
        // TODO add your handling code here:
        ImageIcon II = new ImageIcon(getClass().getResource("/imagens/compilarPress.png"));
        jLabelCompilar.setIcon(II);
        AnaliseLexica();
    }//GEN-LAST:event_jLabelCompilarMousePressed

    private void jLabelCompilarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCompilarMouseExited
        // TODO add your handling code here:
        ImageIcon II = new ImageIcon(getClass().getResource("/imagens/compilar.png"));
        jLabelCompilar.setIcon(II);
    }//GEN-LAST:event_jLabelCompilarMouseExited

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelCompilar;
    private javax.swing.JLabel jLabelExecutar;
    private javax.swing.JMenu jMenuArquivo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEditar;
    private javax.swing.JMenuItem jMenuItemAbrir;
    private javax.swing.JMenuItem jMenuItemColar;
    private javax.swing.JMenuItem jMenuItemCompilar;
    private javax.swing.JMenuItem jMenuItemCopiar;
    private javax.swing.JMenuItem jMenuItemNovo;
    private javax.swing.JMenuItem jMenuItemProjeto;
    private javax.swing.JMenuItem jMenuItemRecortar;
    private javax.swing.JMenuItem jMenuItemSair;
    private javax.swing.JMenuItem jMenuItemSalvar;
    private javax.swing.JMenuItem jMenuItemSalvarComo;
    private javax.swing.JMenu jMenuProjeto;
    private javax.swing.JMenu jMenuSobre;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPaneEdicao;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea;
    private javax.swing.JTextArea jTextAreaSaida;
    // End of variables declaration//GEN-END:variables
}
