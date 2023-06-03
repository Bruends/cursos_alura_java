package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {
    private Connection con;
    ContaDAO(Connection con)
    {
        this.con = con;
    }

    public void save(DadosAberturaConta dadosDaConta)
    {
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);

        String sql = """
                        INSERT INTO conta(numero, saldo, cliente_nome, cliente_cpf, cliente_email)
                        values(?,?,?,?,?)
                      """;

        try {
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, conta.getNumero());
            stmt.setBigDecimal(2, BigDecimal.ZERO);
            stmt.setString(3, cliente.getNome());
            stmt.setString(4, cliente.getCpf());
            stmt.setString(5, cliente.getEmail());

            stmt.execute();
            con.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Set<Conta> listar()
    {
        Set<Conta> contas = new HashSet<>();
        String sql = "SELECT * FROM conta";

        try {
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Integer numeroConta = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dados);
                Conta conta = new Conta(numeroConta, cliente);

                contas.add(conta);
            }

            resultSet.close();
            stmt.close();
            con.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return contas;
    }

}
