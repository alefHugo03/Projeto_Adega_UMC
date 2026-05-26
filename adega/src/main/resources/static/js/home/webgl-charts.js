import * as THREE from 'https://cdn.skypack.dev/three@0.132.2';

/**
 * Cria um gráfico de evolução semanal 3D usando WebGL
 * Ideal para o tema Gold/Black da Adega.
 */
export function renderizarEstoque3D(containerId, vendas) {
    const container = document.getElementById(containerId);
    if (!container) return;

    // 1. Configuração da Cena e Câmera
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, container.clientWidth / container.clientHeight, 0.1, 1000);
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    
    renderer.setSize(container.clientWidth, container.clientHeight);
    container.appendChild(renderer.domElement);

    // Adiciona uma grade no chão para dar contexto de gráfico
    const gridHelper = new THREE.GridHelper(20, 20, 0xD4AF37, 0x333333);
    gridHelper.position.y = 0;
    scene.add(gridHelper);

    // 2. Iluminação (Destaque para o Dourado)
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
    scene.add(ambientLight);
    
    const pointLight = new THREE.PointLight(0xD4AF37, 2);
    pointLight.position.set(10, 10, 10);
    scene.add(pointLight);

    // 3. Materiais e Geometria
    // Material dourado metálico
    const materialGold = new THREE.MeshStandardMaterial({
        color: 0xD4AF37,
        metalness: 0.7,
        roughness: 0.2
    });

    // Lógica de agrupamento por dia (Últimos 7 dias)
    const faturamentoPorDia = {};
    const hoje = new Date();
    for (let i = 6; i >= 0; i--) {
        const d = new Date(hoje);
        d.setDate(hoje.getDate() - i);
        faturamentoPorDia[d.toLocaleDateString('pt-BR').slice(0, 5)] = 0;
    }

    vendas.forEach(v => {
        const data = new Date(v.dataVenda).toLocaleDateString('pt-BR').slice(0, 5);
        if (faturamentoPorDia[data] !== undefined) {
            faturamentoPorDia[data] += (parseFloat(v.valorTotal) || 0);
        }
    });

    const labels = Object.keys(faturamentoPorDia);
    const valores = Object.values(faturamentoPorDia);
    const maxValor = Math.max(...valores, 1);

    const bars = [];
    valores.forEach((valor, index) => {
        // Escala a altura proporcionalmente ao faturamento (máximo 8 unidades de altura)
        const height = (valor / maxValor) * 8 + 0.1; 
        const geometry = new THREE.BoxGeometry(1, height, 1);
        const bar = new THREE.Mesh(geometry, materialGold);
        
        // Posicionamento em linha
        bar.position.x = (index - labels.length / 2) * 2;
        bar.position.y = height / 2;
        bar.position.z = 0;
        
        scene.add(bar);
        bars.push(bar);
    });

    camera.position.set(12, 8, 12);
    camera.lookAt(0, 0, 0);

    // 4. Animação de entrada e rotação
    function animate() {
        requestAnimationFrame(animate);
        
        // Rotação da cena inteira suave para efeito 3D
        scene.rotation.y += 0.005;

        renderer.render(scene, camera);
    }

    animate();

    // Ajuste de redimensionamento
    window.addEventListener('resize', () => {
        const width = container.clientWidth;
        const height = container.clientHeight;
        renderer.setSize(width, height);
        camera.aspect = width / height;
        camera.updateProjectionMatrix();
    });
}