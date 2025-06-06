package Unidad4;

public class Datos {
    private String palabra;
    private int Token;
    private int Id;
    private int pos;
    private int lineaSintaxis;

    public Datos(String palabra, int Token, int Id, int pos) {
        this.palabra = palabra;
        this.Token = Token;
        this.Id = Id;
        this.pos = pos;
    }

    public Datos(String palabra, int pos) {
        this.palabra = palabra;
        this.pos = pos;
    }

    public Datos(String palabra, int Token, int Id, int pos, int lineaSintaxis) {
        this.palabra = palabra;
        this.Token = Token;
        this.Id = Id;
        this.pos = pos;
        this.lineaSintaxis = lineaSintaxis;
    }
    public Datos(String palabra, int linea, int pos) {
        this.palabra = palabra;
        this.lineaSintaxis = linea;
        this.pos = pos;
        this.Token = 0; // o lo que manejes por defecto
    }


    public String getPalabra() {
		return palabra;
	}

	public void setPalabra(String palabra) {
		this.palabra = palabra;
	}

	public int getToken() {
		return Token;
	}
	public void setToken(int token) {
		Token = token;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id= id;
	}
	public int getPos() {
		return pos;
	}
	public void setPosn(int pos) {
		this.pos = pos;
	}

	public int getLineaSintaxis() {
		return lineaSintaxis;
	}

	public void setLineaSintaxis(int lineaSintaxis) {
		this.lineaSintaxis = lineaSintaxis;
	}

	@Override
    public String toString() {
        return (palabra != null && !palabra.isEmpty() ? palabra : "") + "	" +
                (Token != 0 ? +Token : "") + "	" +
                (Id != 0 ? +Id : "") + "	" +
                (pos != 0 ? +pos : "");
    }
}
