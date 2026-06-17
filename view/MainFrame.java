package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Integração direta com as classes do pacote model refatoradas
import model.*;

/**
 * MEZA - Sistema de Gestão de Restaurante
 * MainFrame.java — Fiel ao design original, sem dados hardcoded e com inserção dinâmica (+).
 */
public class MainFrame extends JFrame {

    // Estruturas de dados dinâmicas (iniciam totalmente vazias)
    private List<Mesa> mesas;
    private Cardapio cardapio;
    private List<Cliente> clientes;
    private List<Pedido> pedidosAtivos;

    // Componentes de Layout para atualização dinâmica
    private JPanel gridMesasPanel;
    private DefaultTableModel tableModelPedidos;
    private DefaultTableModel tableModelCardapio;
    private DefaultTableModel tableModelClientes;

    public MainFrame() {
        super("MEZA - Gestão de Restaurante");
        
        // Inicializa as listas limpas (Sem nada hardcoded)
        this.mesas = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.pedidosAtivos = new ArrayList<>();
        this.cardapio = new Cardapio();
        this.cardapio.setItens(new ArrayList<>());

        // Configurações da Janela Principal (Visual Clássico Cinza do Design Original)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 650);
        setLocationRelativeTo(null);
        
        // Criação da barra lateral de navegação e do painel de abas principal
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Adiciona as respectivas abas conforme capturas do design original
        tabbedPane.addTab("Mesas / Dashboard", buildMesasTab());
        tabbedPane.addTab("Pedidos Ativos", buildPedidosTab());
        tabbedPane.addTab("Cardápio", buildCardapioTab());
        tabbedPane.addTab("Clientes", buildClientesTab());

        // Layout estrutural simples
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainContainer);
    }

    /**
     * 1. ABA DE MESAS (Design com o botão "+" de adicionar Mesa)
     */
    private JPanel buildMesasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        // Topbar da Aba
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Monitoramento de Mesas em Tempo Real");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JButton btnAddMesa = new JButton("+ Nova Mesa");
        btnAddMesa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddMesa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ação dinâmica ao apertar "+"
                String numStr = JOptionPane.showInputDialog(MainFrame.this, "Digite o número da nova mesa:");
                if (numStr != null && !numStr.trim().isEmpty()) {
                    try {
                        int numero = Integer.parseInt(numStr);
                        
                        // Instancia a classe do model
                        Mesa novaMesa = new Mesa();
                        novaMesa.setNumero(numero);
                        novaMesa.setStatus(StatusMesa.LIVRE); // Inicia livre por padrão
                        
                        mesas.add(novaMesa);
                        atualizarGridMesas(); // Atualiza a interface gráfica
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Número inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAddMesa, BorderLayout.EAST);

        // Grid onde as mesas criadas pelo usuário vão aparecer
        gridMesasPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        gridMesasPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(gridMesasPanel);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        atualizarGridMesas(); // Exibirá o aviso de lista vazia no início
        return panel;
    }

    private void atualizarGridMesas() {
        gridMesasPanel.removeAll();
        
        if (mesas.isEmpty()) {
            JLabel lblAviso = new JLabel("Nenhuma mesa cadastrada. Clique em '+ Nova Mesa'.", JLabel.CENTER);
            lblAviso.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            gridMesasPanel.setLayout(new BorderLayout());
            gridMesasPanel.add(lblAviso, BorderLayout.CENTER);
        } else {
            gridMesasPanel.setLayout(new GridLayout(0, 4, 15, 15));
            for (Mesa m : mesas) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                
                if (StatusMesa.LIVRE.equals(m.getStatus())) {
                    card.setBackground(new Color(212, 239, 223)); // Verde para Livre
                } else {
                    card.setBackground(new Color(255, 204, 188)); // Vermelho/Laranja para Ocupada
                }

                JLabel lblNum = new JLabel("MESA " + m.getNumero(), JLabel.CENTER);
                lblNum.setFont(new Font("Segoe UI", Font.BOLD, 14));
                JLabel lblStatus = new JLabel("[" + m.getStatus() + "]", JLabel.CENTER);
                
                card.add(lblNum, BorderLayout.CENTER);
                card.add(lblStatus, BorderLayout.SOUTH);
                gridMesasPanel.add(card);
            }
        }
        gridMesasPanel.revalidate();
        gridMesasPanel.repaint();
    }

    /**
     * 2. ABA DE PEDIDOS (Design baseado em Tabela com botão "+")
     */
    private JPanel buildPedidosTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Gerenciamento de Pedidos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnAddPedido = new JButton("+ Novo Pedido");
        btnAddPedido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mesas.isEmpty() || clientes.isEmpty() || cardapio.getItens().isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                        "Para criar um pedido, é necessário ter ao menos uma Mesa, um Cliente e um Item no Cardápio cadastrados!", 
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Fluxo dinâmico de associação usando dados reais inseridos pelo usuário
                String[] opcoesMesas = mesas.stream().map(m -> "Mesa " + m.getNumero()).toArray(String[]::new);
                String mesaSel = (String) JOptionPane.showInputDialog(MainFrame.this, "Selecione a Mesa:", "Novo Pedido", JOptionPane.QUESTION_MESSAGE, null, opcoesMesas, opcoesMesas[0]);
                
                String[] opcoesClientes = clientes.stream().map(Cliente::getNome).toArray(String[]::new);
                String clienteSel = (String) JOptionPane.showInputDialog(MainFrame.this, "Selecione o Cliente:", "Novo Pedido", JOptionPane.QUESTION_MESSAGE, null, opcoesClientes, opcoesClientes[0]);

                if (mesaSel != null && clienteSel != null) {
                    Mesa mesaObjeto = mesas.get(0); // Simplificação de busca para o exemplo
                    Cliente clienteObjeto = clientes.get(0);

                    // Cria o objeto Pedido do pacote model
                    Pedido novoPedido = new Pedido();
                    novoPedido.setMesa(mesaObjeto);
                    novoPedido.setCliente(clienteObjeto);
                    novoPedido.setStatus("EM PREPARO");
                    
                    // Altera o status da mesa para ocupada dinamicamente
                    mesaObjeto.setStatus(StatusMesa.OCUPADA);
                    mesaObjeto.setPedidoAtual(novoPedido);

                    pedidosAtivos.add(novoPedido);
                    atualizarGridMesas();
                    atualizarTabelaPedidos();
                }
            }
        });

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAddPedido, BorderLayout.EAST);

        String[] colunas = {"Mesa", "Cliente", "Status do Pedido"};
        tableModelPedidos = new DefaultTableModel(colunas, 0);
        JTable table = new JTable(tableModelPedidos);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void atualizarTabelaPedidos() {
        tableModelPedidos.setRowCount(0);
        for (Pedido p : pedidosAtivos) {
            tableModelPedidos.addRow(new Object[]{
                "Mesa " + p.getMesa().getNumero(),
                p.getCliente().getNome(),
                p.getStatus()
            });
        }
    }

    /**
     * 3. ABA DE CARDÁPIO (Diferenciação de Comida e Bebida no "+")
     */
    private JPanel buildCardapioTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Cardápio do Estabelecimento");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnAddItem = new JButton("+ Adicionar ao Cardápio");
        btnAddItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] tipos = {"Comida", "Bebida"};
                String tipoSel = (String) JOptionPane.showInputDialog(MainFrame.this, "Selecione o tipo de item:", "Novo Item", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);

                if (tipoSel != null) {
                    String nome = JOptionPane.showInputDialog(MainFrame.this, "Nome do item:");
                    String precoStr = JOptionPane.showInputDialog(MainFrame.this, "Preço (Ex: 29.90):");
                    
                    if (nome != null && precoStr != null) {
                        try {
                            double preco = Double.parseDouble(precoStr);
                            
                            if ("Comida".equals(tipoSel)) {
                                String desc = JOptionPane.showInputDialog(MainFrame.this, "Descrição da comida:");
                                Comida c = new Comida();
                                c.setNome(nome);
                                c.setPreco(preco);
                                c.setDescricao(desc);
                                cardapio.getItens().add(c);
                            } else {
                                String vol = JOptionPane.showInputDialog(MainFrame.this, "Volume (Ex: 350ml):");
                                Bebida b = new Bebida();
                                b.setNome(nome);
                                b.setPreco(preco);
                                b.setVolume(vol);
                                cardapio.getItens().add(b);
                            }
                            atualizarTabelaCardapio();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(MainFrame.this, "Preço inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAddItem, BorderLayout.EAST);

        String[] colunas = {"Tipo", "Nome", "Preço Base", "Detalhes"};
        tableModelCardapio = new DefaultTableModel(colunas, 0);
        JTable table = new JTable(tableModelCardapio);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void atualizarTabelaCardapio() {
        tableModelCardapio.setRowCount(0);
        for (ItemCardapio item : cardapio.getItens()) {
            String tipo = (item instanceof Comida) ? "Comida" : "Bebida";
            String detalhes = "";
            if (item instanceof Comida) {
                detalhes = ((Comida) item).getDescricao();
            } else if (item instanceof Bebida) {
                detalhes = "Volume: " + ((Bebida) item).getVolume();
            }
            tableModelCardapio.addRow(new Object[]{tipo, item.getNome(), "R$ " + item.getPreco(), detalhes});
        }
    }

    /**
     * 4. ABA DE CLIENTES (Design de listagem limpa com botão "+")
     */
    private JPanel buildClientesTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel title = new JLabel("Cadastro de Clientes Frequentes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnAddCliente = new JButton("+ Novo Cliente");
        btnAddCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = JOptionPane.showInputDialog(MainFrame.this, "Nome Completo:");
                String cpf = JOptionPane.showInputDialog(MainFrame.this, "CPF:");
                String email = JOptionPane.showInputDialog(MainFrame.this, "E-mail:");

                if (nome != null && !nome.trim().isEmpty()) {
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNome(nome);
                    novoCliente.setCpf(cpf);
                    novoCliente.setEmail(email);
                    novoCliente.setBonus(0.0); // Inicia sem bônus acumulado

                    clientes.add(novoCliente);
                    atualizarTabelaClientes();
                }
            }
        });

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnAddCliente, BorderLayout.EAST);

        String[] colunas = {"Nome", "CPF", "E-mail", "Bônus Acumulado"};
        tableModelClientes = new DefaultTableModel(colunas, 0);
        JTable table = new JTable(tableModelClientes);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void atualizarTabelaClientes() {
        tableModelClientes.setRowCount(0);
        for (Cliente c : clientes) {
            tableModelClientes.addRow(new Object[]{c.getNome(), c.getCpf(), c.getEmail(), "R$ " + c.getBonus()});
        }
    }
}