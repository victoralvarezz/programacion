package ejercicio1;

public class persona {

	public static abstract class Personaje {
		protected String nombre;
		protected int vida;
		protected int ataque;
		protected int defensa;
		protected int mana;

		public Personaje(String nombre, int vida, int ataque, int defensa, int mana) {
			this.nombre = nombre;
			this.vida = vida;
			this.ataque = ataque;
			this.defensa = defensa;
			this.mana = mana;
		}

		public void mostrarInfo() {
			System.out.println(
					nombre + " | Vida: " + vida + " | Atk: " + ataque + " | Def: " + defensa + " | Mana: " + mana);
		}

		public boolean estaVivo() {
			return vida > 0;
		}

		// recibe daño teniendo en cuenta la defensa
		public void recibirDanio(int danio) {
			int real = danio - defensa;
			if (real < 1)
				real = 1; // esto es para que siempre hagas algo de daño 
			vida -= real;
			if (vida < 0)
				vida = 0;
		}

		// ataque a 1 enemigo
		protected void pegarA(Personaje objetivo, int danioBase) {
			if (objetivo == null || !objetivo.estaVivo())
				return;

			System.out.println(nombre + " ataca a " + objetivo.nombre);
			objetivo.recibirDanio(danioBase);
			System.out.println("   -> " + objetivo.nombre + " queda con vida " + objetivo.vida);
		}

		// ataque a todos (área)
		protected void pegarATodos(Personaje[] enemigos, int danioBase) {
			System.out.println(nombre + " usa ataque en área");
			for (int i = 0; i < enemigos.length; i++) {
				if (enemigos[i] != null && enemigos[i].estaVivo()) {
					enemigos[i].recibirDanio(danioBase);
					System.out.println("   -> " + enemigos[i].nombre + " queda con vida " + enemigos[i].vida);
				}
			}
		}

		// devuelve el primer personaje vivo del equipo
		protected Personaje primerVivo(Personaje[] equipo) {
			for (int i = 0; i < equipo.length; i++) {
				if (equipo[i] != null && equipo[i].estaVivo())
					return equipo[i];
			}
			return null;
		}

		// cuenta cuántos vivos hay en un equipo
		protected int contarVivos(Personaje[] equipo) {
			int c = 0;
			for (int i = 0; i < equipo.length; i++) {
				if (equipo[i] != null && equipo[i].estaVivo())
					c++;
			}
			return c;
		}

		// cada personaje decide su turno 
		public abstract void hacerTurno(Personaje[] enemigos, Personaje[] aliados);
	}

	// =======================
	// EQUIPO LUZ (BUENOS)
	// =======================

	// JEDI = Guerrero (tanque + daño estable)
	// Ejemplo nombre: "Yoda"
	public static class Jedi extends Personaje {

		public Jedi(String nombre) {
			super(nombre, 130, 24, 8, 20); // vida alta y defensa alta
		}

		@Override
		public void hacerTurno(Personaje[] enemigos, Personaje[] aliados) {
			// simple: pega al primero vivo
			System.out.println(nombre + " usa sable de luz");
			pegarA(primerVivo(enemigos), ataque);
		}
	}

	// SOLDADO REBELDE = daño a distancia constante
	// Ejemplo nombre: "Han Solo"
	public static class SoldadoRebelde extends Personaje {

		public SoldadoRebelde(String nombre) {
			super(nombre, 110, 22, 4, 0);
		}

		@Override
		public void hacerTurno(Personaje[] enemigos, Personaje[] aliados) {
			// simple: dispara
			System.out.println(nombre + " dispara blaster");
			pegarA(primerVivo(enemigos), ataque);
		}
	}

	// SANADOR = Sacerdote (cura al aliado más débil)
	//Seria  "Leia"
	public static class Sanador extends Personaje {

		public Sanador(String nombre) {
			super(nombre, 115, 10, 5, 40); // mana alto para curar
		}

		@Override
		public void hacerTurno(Personaje[] enemigos, Personaje[] aliados) {

			Personaje aliado = aliadoMasDebil(aliados);

			// si alguien tiene poca vida -> cura(+12)
			if (aliado != null && aliado.vida < 70 && mana >= 12) {
				System.out.println(nombre + " cura a " + aliado.nombre);
				mana -= 12;
				aliado.vida += 20;
				System.out.println("   -> " + aliado.nombre + " ahora tiene " + aliado.vida + " | Mana: " + mana);
			} else {
				// si no, haced daño 10 
				System.out.println(nombre + " golpea débilmente");
				pegarA(primerVivo(enemigos), 10);
			}
		}

		// busca el aliado vivo con menos vida
		private Personaje aliadoMasDebil(Personaje[] aliados) {
			Personaje min = null;

			for (int i = 0; i < aliados.length; i++) {
				if (aliados[i] != null && aliados[i].estaVivo()) {
					if (min == null || aliados[i].vida < min.vida) {
						min = aliados[i];
					}
				}
			}
			return min;
		}
	}


	// SITH = Guerrero oscuro ataque a dos personnas
	// Ejemplo nombre: "Darth Vader" (aplastamiento con la Fuerza)
	public static class Sith extends Personaje {

		public Sith(String nombre) {
			super(nombre, 125, 26, 7, 25);
		}

		@Override
		public void hacerTurno(Personaje[] enemigos, Personaje[] aliados) {

			int vivos = contarVivos(enemigos);

			// para dos enemigos en area 
			if (vivos >= 2 && mana >= 10) {
				mana -= 10;
				System.out.println(nombre + " usa fuerza oscura (área) (-10 mana)");
				pegarATodos(enemigos, 12);
			} else {
				System.out.println(nombre + " ataca con sable rojo");
				pegarA(primerVivo(enemigos), ataque);
			}
		}
	}

	// SOLDADO IMPERIAL = disparo simple
	// Ejemplo nombre: "Stormtrooper"
	public static class SoldadoImperial extends Personaje {

		public SoldadoImperial(String nombre) {
			super(nombre, 110, 20, 5, 0);
		}

		@Override
		public void hacerTurno(Personaje[] enemigos, Personaje[] aliados) {
			System.out.println(nombre + " dispara");
			pegarA(primerVivo(enemigos), ataque);
		}
	}

	// CAZARRECOMPENSAS = mago
	// Ejemplo nombre:"Boba Fett"  tengo que añadir la clase (quemadura)
	// nota :  quema -5 para el estado 
	public static class Cazarrecompensas extends Personaje {

		public Cazarrecompensas(String nombre) {
			super(nombre, 100, 18, 4, 30);
		}

		@Override
		public void hacerTurno(Personaje[] enemigos, Personaje[] aliados) {

			Personaje obj = primerVivo(enemigos);
			if (obj == null)
				return;

			// habilidad especial (quemadura)
			if (mana >= 12) {
				mana -= 12;
				System.out.println(nombre + " lanza habilidad especial (-12 mana)");
				pegarA(obj, 12);
			} else {
				System.out.println(nombre + " dispara");
				pegarA(obj, ataque);
			}
		}
	}

	
}
