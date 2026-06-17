public class Cliente {
    private String nome;
    private String cpf;
    private String email;
    private double bonus;

    public Cliente(String nome, String cpf, String email) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.bonus = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBonus() {
        return bonus;
    }

    public void adicionarBonus(double valor) {
        this.bonus += valor * 0.10;
    }

    public void usarBonus(double valor) {
        if (bonus >= valor) {
            bonus -= valor;
        }
    }
}