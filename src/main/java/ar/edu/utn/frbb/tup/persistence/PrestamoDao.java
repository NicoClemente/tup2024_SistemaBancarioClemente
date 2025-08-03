package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Prestamo;
import ar.edu.utn.frbb.tup.persistence.entity.PrestamoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class PrestamoDao extends AbstractBaseDao {

    @Override
    protected String getEntityName() {
        return "PRESTAMO";
    }

    public void save(Prestamo prestamo) {
        if (prestamo.getId() == null) {
            prestamo.setId(Math.abs(new Random().nextLong()));
        }
        PrestamoEntity entity = new PrestamoEntity(prestamo);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public Prestamo find(long id) {
        if (getInMemoryDatabase().get(id) == null) {
            return null;
        }
        return ((PrestamoEntity) getInMemoryDatabase().get(id)).toPrestamo();
    }

    public List<Prestamo> findByNumeroCliente(long numeroCliente) {
        List<Prestamo> prestamosDelCliente = new ArrayList<>();
        for (Object object : getInMemoryDatabase().values()) {
            PrestamoEntity prestamo = ((PrestamoEntity) object);
            if (prestamo.getNumeroCliente().equals(numeroCliente)) {
                prestamosDelCliente.add(prestamo.toPrestamo());
            }
        }
        return prestamosDelCliente;
    }

    public List<Prestamo> findAll() {
        List<Prestamo> prestamos = new ArrayList<>();
        for (Object object : getInMemoryDatabase().values()) {
            PrestamoEntity prestamoEntity = (PrestamoEntity) object;
            prestamos.add(prestamoEntity.toPrestamo());
        }
        return prestamos;
    }
}