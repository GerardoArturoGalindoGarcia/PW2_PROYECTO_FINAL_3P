package com.example.sistemafacturacion.data;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String rtn;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String estado;

    public Cliente() {
    }

    public Cliente(int idCliente, String nombre, String rtn, String email, String telefono,
                   String direccion, String ciudad, String estado) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.rtn = rtn;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.estado = estado;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRtn() { return rtn; }
    public void setRtn(String rtn) { this.rtn = rtn; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
