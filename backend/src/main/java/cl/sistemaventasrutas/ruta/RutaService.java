package cl.sistemaventasrutas.ruta;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RutaService {

    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    public RutaResponse crear(RutaRequest request) {

        if (rutaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new IllegalArgumentException(
                    "Ya existe una ruta con ese nombre");
        }

        Ruta ruta = new Ruta(request.nombre());

        ruta = rutaRepository.save(ruta);

        return RutaResponse.from(ruta);
    }

    @Transactional(readOnly = true)
    public List<RutaResponse> listar() {

        return rutaRepository
                .findAllByActivoTrueOrderByNombreAsc()
                .stream()
                .map(RutaResponse::from)
                .toList();
    }

    public void eliminar(Long id) {

        Ruta ruta = rutaRepository
                .findByIdAndActivoTrue(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Ruta no encontrada"));

        ruta.desactivar();
    }
}