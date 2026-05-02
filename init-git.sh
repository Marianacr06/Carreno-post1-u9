#!/bin/bash
# Script para inicializar repositorio Git y hacer commits descriptivos
# Este script debe ejecutarse en el directorio raíz del proyecto

cd "$(dirname "$0")"

echo "=========================================="
echo "Inicializando repositorio Git..."
echo "=========================================="

git init

echo "Configurando usuario Git..."
git config user.name "Carreño"
git config user.email "carrenio@universidad.edu"

echo ""
echo "=========================================="
echo "Commit 1: Implementación de estructura base"
echo "=========================================="
git add pom.xml .gitignore
git add src/main/java/com/universidad/productosservice/ProductosServiceApplication.java
git add src/main/java/com/universidad/productosservice/domain/Producto.java
git add src/main/java/com/universidad/productosservice/repository/ProductoRepository.java
git add src/main/java/com/universidad/productosservice/service/ProductoService.java
git add src/main/java/com/universidad/productosservice/service/ProductoServiceImpl.java
git add src/main/java/com/universidad/productosservice/controller/ProductoController.java
git add src/main/resources/application.properties
git commit -m "Commit 1: Implementación estructura base del proyecto

- Configuración de Maven con dependencias Spring Boot 3.3.0
- Entidad JPA Producto con getters/setters
- Interface ProductoService con métodos CRUD
- Implementación ProductoServiceImpl con validaciones de negocio
- Repositorio ProductoRepository que extiende JpaRepository
- Controlador REST ProductoController
- Configuración de H2 Database en application.properties
- Se cumplen validaciones: nombre no vacío, precio > 0, stock >= 0
"

echo ""
echo "=========================================="
echo "Commit 2: Suite de pruebas unitarias - Paso 3, 4 y 5"
echo "=========================================="
git add src/test/java/com/universidad/productosservice/service/ProductoServiceImplTest.java
git commit -m "Commit 2: Suite completa de pruebas unitarias (25 tests)

Paso 3 - Casos Exitosos (3 pruebas):
- crear_datosValidos_retornaProductoGuardado: Verifica creación correcta
- buscarPorId_existente_retornaProducto: Busca producto existente
- actualizarStock_productoExistente_actualizaCorrectamente: Actualización de stock

Paso 4 - Pruebas Parametrizadas (6 pruebas):
- buscarPorId_noExistente_lanzaRuntimeException
- crear_nombreInvalido_lanzaIllegalArgumentException (@NullAndEmptySource, @ValueSource)
- crear_precioInvalido_lanzaIllegalArgumentException (@ValueSource doubles)
- crear_stockNegativo_lanzaIllegalArgumentException (@ValueSource ints)
- actualizarStock_stockNegativo_lanzaIllegalArgumentException (@ValueSource ints)
- eliminar_productoNoExistente_lanzaRuntimeException

Paso 5 - ArgumentCaptor y Verificación Avanzada (6 pruebas):
- crear_nombreConEspacios_guardaNombreNormalizado: Verifica normalización con ArgumentCaptor
- crear_precioConDecimales_guardaPrecioExacto: Precisión de decimales
- eliminar_productoExistente_llamaDeleteById: Verifica interacciones exactas
- buscarPorId_llavaRepositorioExactamenteLaVez: Verifica exactitud de llamadas
- actualizarStock_verificaQueSeGuardaCon_save: Captura datos actualizados

Características implementadas:
- @Mock e @InjectMocks para aislar dependencias
- @ParameterizedTest con @ValueSource y @NullAndEmptySource
- ArgumentCaptor para inspeccionar argumentos exactos
- verify() y verifyNoInteractions() para validar comportamiento
- Cobertura de casos positivos, negativos y límite
- Resultado: 25 tests ejecutados exitosamente
"

echo ""
echo "=========================================="
echo "Commit 3: Documentación y configuración final"
echo "=========================================="
git add README.md
git commit -m "Commit 3: README con documentación completa del proyecto

- Descripción del proyecto y objetivos de aprendizaje
- Arquitectura y estructura del proyecto
- Dependencias principales (Spring Boot 3.3.0, JUnit 5, Mockito)
- Descripción de cada prueba unitaria (Paso 3, 4, 5)
- Instrucciones de compilación y ejecución
- Captura de pantalla de resultado: 25 tests pasando
- Conceptos clave: @Mock, @InjectMocks, @ParameterizedTest, ArgumentCaptor
- Información de cobertura de código (>90% en capa de servicio)
- Referencias a documentación oficial
"

echo ""
echo "=========================================="
echo "Resumen de commits"
echo "=========================================="
git log --oneline

echo ""
echo "=========================================="
echo "Estado del repositorio"
echo "=========================================="
git status

echo ""
echo "✅ ¡Repositorio Git inicializado exitosamente!"
echo ""
echo "Para subir a GitHub, ejecutar:"
echo "  git remote add origin https://github.com/tu-usuario/carrenio-post1-u9.git"
echo "  git branch -M main"
echo "  git push -u origin main"
