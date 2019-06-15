package br.edu.udc.pdi.curva.janela;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import br.edu.udc.pdi.curva.math.PdiMath;
import br.edu.udc.pdi.curva.objetos3D.Cubo3D;
import br.edu.udc.pdi.curva.objetos3D.Ponto2D;
import br.edu.udc.pdi.curva.objetos3D.Ponto3D;


public class Frame extends JFrame {
	private static final long serialVersionUID = -209859810682587192L;

	Timer timer;

	public Frame() {
		setTitle("Trabalho de PDI: Curva de Bezier");
		setSize(1200, 900);
		setLayout(new BorderLayout());

		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);


		final PaintPanel paintPan = new PaintPanel(lerPontos(),lerPontos(),lerPontos(),lerPontos());
		add(paintPan, BorderLayout.CENTER);

		JMenu barraIniciar = new JMenu("Inicialização");
		menubar.add(barraIniciar);

		JMenuItem mntmInicio = new JMenuItem("start");
		mntmInicio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				timer.start();
			}
		});
		barraIniciar.add(mntmInicio);

		JMenuItem mntmStop = new JMenuItem("Stop");
		mntmStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Frame.this.timer.stop();
			}
		});
		barraIniciar.add(mntmStop);

		final int segundos = 200;
		this.timer = new Timer(segundos, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paintPan.updateGraphics(50, 50);
				repaint();
			}
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new Frame();
	}
	public static Ponto3D lerPontos() {
		String strD = JOptionPane.showInputDialog("Digite as coordenado: Ex 100 -100 20");

		String [] str1 = strD.split(" ");

		int x = Integer.parseInt(str1[0]);
		int y = Integer.parseInt(str1[1]);
		int z = Integer.parseInt(str1[2]);

		return new Ponto3D(x,y,z);
	}
}
class PaintPanel extends JPanel {

	private static final long serialVersionUID = 8064963544161103218L;

	private final Ponto3D camera = new Ponto3D(0, 0, 1000); //Ponto de Observação do mundo tridimensional

	private final int numeroPontos = 200;//defini quantos pontos vai ser platado da curva

	private double anguloYAnterior = 0;
	private double anguloZAnterior = 0;

	private int pontoAtual = 0;
	private final Ponto3D[] pontosCurva;//vetor para guardar pontos da curva

	private final Cubo3D cubo = new Cubo3D(30);//tamanho do cubo

	public PaintPanel( Ponto3D inicio, Ponto3D controle1, Ponto3D controle2, Ponto3D fim ) {

		setBackground(Color.WHITE);

		Ponto3D[] arg = { inicio, controle1, controle2, fim };
		this.pontosCurva = PdiMath.bezierCurve(arg, this.numeroPontos);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.desenhaCubo(g); // desenha cubo

		for (Ponto3D ponto : pontosCurva) {
			double distancia = this.camera.distancia(ponto);
			Ponto2D pontoProjetado = this.pespectiva(ponto, this.camera, distancia);

			this.desenhaPonto(pontoProjetado.x, pontoProjetado.y, 2, g); //desenha pontos conrespondente a curva
		}
	}

	public void updateGraphics(int length, int width) {

		final int posicaoAtual = this.pontoAtual;
		final int proximaPosicao = this.pontoAtual + 1;

		if (proximaPosicao < this.pontosCurva.length) {

			// EM Y
			final double catetoOpostoY = this.pontosCurva[proximaPosicao].z - this.pontosCurva[posicaoAtual].z;
			final double catetoAdjacenteY = this.pontosCurva[proximaPosicao].x - this.pontosCurva[posicaoAtual].x;

			final double anguloYRad = Math.atan(catetoOpostoY / catetoAdjacenteY);
			final double anguloY = Math.toDegrees(anguloYRad);

			this.cubo.rotacao(this.anguloYAnterior - anguloY, Cubo3D.eixoRotacao.Y);
			this.anguloYAnterior = anguloY;

			// EM Z
			final double catetoOpostoZ = this.pontosCurva[proximaPosicao].y - this.pontosCurva[posicaoAtual].y;
			final double catetoAdjacenteZ = this.pontosCurva[proximaPosicao].x - this.pontosCurva[posicaoAtual].x;

			final double anguloZRad = Math.atan(catetoOpostoZ / catetoAdjacenteZ);
			final double anguloZ = Math.toDegrees(anguloZRad);

			this.cubo.rotacao(this.anguloZAnterior - anguloZ, Cubo3D.eixoRotacao.Z);
			this.anguloZAnterior = anguloZ;
		}

		this.pontoAtual++;
		this.pontoAtual = this.pontoAtual % this.numeroPontos;

		final double x = this.pontosCurva[(this.pontoAtual + 1) % this.numeroPontos].x - this.cubo.centro.x;
		final double y = this.pontosCurva[(this.pontoAtual + 1) % this.numeroPontos].y - this.cubo.centro.y;
		final double z = this.pontosCurva[(this.pontoAtual + 1) % this.numeroPontos].z - this.cubo.centro.z;

		this.cubo.translado(x, y, z);//recalcula translação

		repaint();
	}
	
	public Ponto2D pespectiva(Ponto3D ponto, Ponto3D camera, double distancia) {
		final double x = ((distancia * (ponto.x - camera.x)) / (distancia + ponto.z)) + camera.x;
		final double y = ((distancia * (ponto.y - camera.y)) / (distancia + ponto.z)) + camera.y;

		return new Ponto2D((int) x, (int) y);
	}

	//metodo que desenha o cubo usando vetor com os pontos que representa vertices no plano que esta sendo visualizado
	public void desenhaCubo(Graphics g) {

		Ponto2D[] projetados = new Ponto2D[8];

		for (int i = 0; i < 8; i++) {
			double distancia = this.camera.distancia(this.cubo.vertices[i]);
			projetados[i] = this.pespectiva(this.cubo.vertices[i], this.camera, distancia);
		}

		this.desenhaLinha(projetados[0].x, projetados[0].y, projetados[1].x, projetados[1].y, g);
		this.desenhaLinha(projetados[1].x, projetados[1].y, projetados[2].x, projetados[2].y, g);
		this.desenhaLinha(projetados[2].x, projetados[2].y, projetados[3].x, projetados[3].y, g);
		this.desenhaLinha(projetados[3].x, projetados[3].y, projetados[0].x, projetados[0].y, g);

		this.desenhaLinha(projetados[4].x, projetados[4].y, projetados[5].x, projetados[5].y, g);
		this.desenhaLinha(projetados[5].x, projetados[5].y, projetados[6].x, projetados[6].y, g);
		this.desenhaLinha(projetados[6].x, projetados[6].y, projetados[7].x, projetados[7].y, g);
		this.desenhaLinha(projetados[7].x, projetados[7].y, projetados[4].x, projetados[4].y, g);

		this.desenhaLinha(projetados[0].x, projetados[0].y, projetados[4].x, projetados[4].y, g);
		this.desenhaLinha(projetados[1].x, projetados[1].y, projetados[5].x, projetados[5].y, g);
		this.desenhaLinha(projetados[2].x, projetados[2].y, projetados[6].x, projetados[6].y, g);
		this.desenhaLinha(projetados[3].x, projetados[3].y, projetados[7].x, projetados[7].y, g);
	}

	//metodo que desenha ponto
	public void desenhaPonto(int x, int y, int raio, Graphics g) {

		x += 400;
		y *= -1;
		y += 300;

		g.setColor(Color.BLACK);
		g.fillOval(x, y, raio, raio);
		g.drawOval(x, y, raio, raio);
	}

	//metodo que desenha linha 
	public void desenhaLinha(int x1, int y1, int x2, int y2, Graphics g) {

		x1 += 400;
		y1 *= -1;
		y1 += 300;

		x2 += 400;
		y2 *= -1;
		y2 += 300;

		g.setColor(Color.BLACK);
		g.drawLine(x1, y1, x2, y2);
	}
}