package com.universidad.productosservice.service;

import com.universidad.productosservice.domain.Producto;
import com.universidad.productosservice.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para ProductoServiceImpl.
 * Utiliza JUnit 5, Mockito y MockitoExtension para aislar la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    // ==================== PASO 3: CASOS EXITOSOS ====================

    @Test
    void crear_datosValidos_retornaProductoGuardado() {
        // Arrange
        Producto guardado = new Producto(1L, "Laptop", 1500.0, 10);
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        // Act
        Producto resultado = productoService.crear("Laptop", 1500.0, 10);

        // Assert
        assertNotNull(resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        assertEquals(1500.0, resultado.getPrecio());
        assertEquals(10, resultado.getStock());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void buscarPorId_existente_retornaProducto() {
        // Arrange
        Producto producto = new Producto(1L, "Mouse", 50.0, 100);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        Producto resultado = productoService.buscarPorId(1L);

        // Assert
        assertEquals("Mouse", resultado.getNombre());
        assertEquals(50.0, resultado.getPrecio());
        assertEquals(100, resultado.getStock());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void actualizarStock_productoExistente_actualizaCorrectamente() {
        // Arrange
        Producto producto = new Producto(1L, "Teclado", 80.0, 20);
        Producto actualizado = new Producto(1L, "Teclado", 80.0, 50);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        // Act
        Producto resultado = productoService.actualizarStock(1L, 50);

        // Assert
        assertEquals(50, resultado.getStock());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    // ==================== PASO 4: PRUEBAS DE ERROR ====================

    @Test
    void buscarPorId_noExistente_lanzaRuntimeException() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> productoService.buscarPorId(99L));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void crear_nombreInvalido_lanzaIllegalArgumentException(String nombre) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear(nombre, 100.0, 5));

        // El repositorio NO debe ser llamado
        verifyNoInteractions(productoRepository);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -100.0, -0.01})
    void crear_precioInvalido_lanzaIllegalArgumentException(double precio) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear("Producto", precio, 5));

        verifyNoInteractions(productoRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void crear_stockNegativo_lanzaIllegalArgumentException(int stock) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productoService.crear("Producto", 100.0, stock));

        verifyNoInteractions(productoRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void actualizarStock_stockNegativo_lanzaIllegalArgumentException(int nuevoStock) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productoService.actualizarStock(1L, nuevoStock));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_productoNoExistente_lanzaRuntimeException() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> productoService.eliminar(99L));

        verify(productoRepository, never()).deleteById(any());
    }

    // ==================== PASO 5: ARGUMENT CAPTOR ====================

    @Test
    void crear_nombreConEspacios_guardaNombreNormalizado() {
        // Arrange
        Producto productoGuardado = new Producto(1L, "Laptop Pro", 1500.0, 5);
        when(productoRepository.save(any())).thenReturn(productoGuardado);

        // Act
        productoService.crear(" Laptop Pro ", 1500.0, 5);

        // Assert - Capturar y verificar el producto exacto
        verify(productoRepository).save(productoCaptor.capture());
        Producto capturado = productoCaptor.getValue();

        assertEquals("Laptop Pro", capturado.getNombre()); // nombre normalizado
        assertEquals(1500.0, capturado.getPrecio());
        assertNull(capturado.getId()); // id es null antes de persistir en el repository
    }

    @Test
    void crear_precioConDecimales_guardaPrecioExacto() {
        // Arrange
        when(productoRepository.save(any())).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Act
        productoService.crear("Producto", 99.99, 10);

        // Assert
        verify(productoRepository).save(productoCaptor.capture());
        Producto capturado = productoCaptor.getValue();

        assertEquals(99.99, capturado.getPrecio());
    }

    @Test
    void eliminar_productoExistente_llamaDeleteById() {
        // Arrange
        Producto producto = new Producto(1L, "Teclado", 80.0, 20);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).deleteById(1L);

        // Act
        productoService.eliminar(1L);

        // Assert
        verify(productoRepository, times(1)).deleteById(1L);
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_llavaRepositorioExactamenteLaVez() {
        // Arrange
        Producto producto = new Producto(2L, "Mouse Inalámbrico", 45.50, 50);
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto));

        // Act
        productoService.buscarPorId(2L);

        // Assert
        verify(productoRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(productoRepository);
    }

    @Test
    void actualizarStock_verificaQueSeGuardaCon_save() {
        // Arrange
        Producto productoOriginal = new Producto(1L, "Monitor", 300.0, 15);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoOriginal));
        when(productoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        productoService.actualizarStock(1L, 25);

        // Assert
        verify(productoRepository).save(productoCaptor.capture());
        Producto capturado = productoCaptor.getValue();

        assertEquals(25, capturado.getStock());
        assertEquals("Monitor", capturado.getNombre());
    }

}
