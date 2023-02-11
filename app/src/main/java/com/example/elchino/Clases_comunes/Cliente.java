package com.example.elchino.Clases_comunes;

public class Cliente {

    protected String id;
    protected String Nombre;
    protected String Apellido1;
    protected String Apellido2;
    protected String Apodo;
    protected String Telefono1;
    protected String Telefono2;
    protected String Notas;
    protected String Direccion;
    protected String Estado;
    protected Integer MontoAprobado;
    protected String Puntuacion;

    public Cliente() {

    }

    public Cliente(String id, String nombre, String apellido1, String apellido2, String apodo, String telefono1, String telefono2, String notas, String direccion, int montoAprobado, String puntuacion, String estado) {
        this.id = id;
        this.Nombre = nombre;
        this.Apellido1 = apellido1;
        this.Apellido2 = apellido2;
        this.Apodo = apodo;
        this.Telefono1 = telefono1;
        this.Telefono2 = telefono2;
        this.Notas = notas;
        this.Direccion = direccion;
        this.MontoAprobado = montoAprobado;
        this.Estado = estado;
        this.Puntuacion = puntuacion;
    }

    public String getId() {
        return id;
    }

    public String getNombre () {
        return Nombre;
    }

    public String getApellido1 () {
        return Apellido1;
    }

    public String getApellido2 () {
        return Apellido2;
    }

    public String getApodo () {
        return Apodo;
    }

    public String getTelefono1 () {
        return Telefono1;
    }

    public String getTelefono2 () {
        return Telefono2;
    }

    public String getNotas () {
        return Notas;
    }

    public int getMontoAprobado () {
        return MontoAprobado;
    }

    public String getDireccion () {
        return Direccion;
    }

    public String getPuntuacion () {
        return Puntuacion;
    }

    public void editarCliente () {
        //Metodo para editar valores del cliente.
    }

    public void borrarCliente () {
        //Metodo que hace desaparecer un cliente.
    }

    public void calificarCliente () {
        //Metodo que se agregara en el futuro para calificar al cliente.
        //Este metodo cambia la puntiacion actual del cliente por caficacion que hace el vendedor.
    }

}
