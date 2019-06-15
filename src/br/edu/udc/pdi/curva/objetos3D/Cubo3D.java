package br.edu.udc.pdi.curva.objetos3D;

import br.edu.udc.pdi.curva.math.PdiMath;

public class Cubo3D {

	public enum eixoRotacao {
		X, Y, Z;
	}

	public Ponto3D centro;
	public Ponto3D[] vertices = new Ponto3D[8];

	public Cubo3D(double medida) {
		this.centro = new Ponto3D(0, 0, 0);

		vertices[0] = new Ponto3D(medida, medida, medida);
		vertices[1] = new Ponto3D(-medida, medida, medida);
		vertices[2] = new Ponto3D(-medida, -medida, medida);
		vertices[3] = new Ponto3D(medida, -medida, medida);
		vertices[4] = new Ponto3D(medida, medida, -medida);
		vertices[5] = new Ponto3D(-medida, medida, -medida);
		vertices[6] = new Ponto3D(-medida, -medida, -medida);
		vertices[7] = new Ponto3D(medida, -medida, -medida);
	}
	
	//Metodo que faz transla��o usando somas
	public void translado(double x, double y, double z) {

		this.centro.x += x;
		this.centro.y += y;
		this.centro.z += z;

		for (int i = 0; i < 8; i++) {
			this.vertices[i].x += x;
			this.vertices[i].y += y;
			this.vertices[i].z += z;
		}
	}

	public void rotacao(double angulo, eixoRotacao eixo) {

		angulo = angulo % 360D;

		final Ponto3D centroAtual = new Ponto3D(this.centro.x, this.centro.y, this.centro.z);
		final double rad = Math.toRadians(angulo);
		final double[][] matrizRotacaoZ = { { Math.cos(rad), -Math.sin(rad), 0.0 },
				{ Math.sin(rad), Math.cos(rad), 0.0 }, { 0.0, 0.0, 1.0 } }; //matriz de transforma��o em Z
		final double[][] matrizRotacaoY = { { Math.cos(rad), 0.0, -Math.sin(rad) }, { 0.0, 1.0, 0.0 },
				{ Math.sin(rad), 0.0, Math.cos(rad) } };//matriz de transforma��o em Y
		final double[][] matrizRotacaoX = { { 1.0, 0.0, 0.0 }, { 0.0, Math.cos(rad), -Math.sin(rad) },
				{ 0.0, Math.sin(rad), Math.cos(rad) } }; //matriz de transfomra��o X

		this.translado(-centroAtual.x, -centroAtual.y, -centroAtual.z); //

		for (int i = 0; i < 8; i++) { //atribuir pontos do vertices para vetor
			final double[][] pontoEmVetor = new double[1][3];
			pontoEmVetor[0][0] = this.vertices[i].x;
			pontoEmVetor[0][1] = this.vertices[i].y;
			pontoEmVetor[0][2] = this.vertices[i].z;

			double[][] resultado = null;
			switch (eixo) {
			case X:
				resultado = PdiMath.multiplicacaoMatriz(pontoEmVetor, matrizRotacaoX); //matriplica��o vetor com matriz de conta��o em X
				break;
			case Y:
				resultado = PdiMath.multiplicacaoMatriz(pontoEmVetor, matrizRotacaoY); //matriplica��o vetor com matriz de conta��o em X
				break;
			case Z:
				resultado = PdiMath.multiplicacaoMatriz(pontoEmVetor, matrizRotacaoZ);//matriplica��o vetor com matriz de conta��o em X
				break;
			}

			this.vertices[i].x = resultado[0][0];
			this.vertices[i].y = resultado[0][1];
			this.vertices[i].z = resultado[0][2];
		}

		this.translado(centroAtual.x, centroAtual.y, centroAtual.z);//faz transla��o para voltar ponto original
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		for (Ponto3D ponto : vertices) {
			sb.append(ponto.toString());
			sb.append(", ");
		}

		return sb.substring(0, sb.length() - 2).toString();
	}
}
