package instructions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

sealed abstract class Instruction permits LoadConstant, Decrement, Multiply, JumpIfZero, Jump, Halt {
	
	abstract void execute(Machine machine);
}

final class LoadConstant extends Instruction {
	int r;
	int c;
	
	LoadConstant(int r, int c) {
		this.r = r;
		this.c = c;
	}
	
	void execute(Machine machine) {
		machine.registers[r] = c;
		machine.pc++;
	}
}

final class JumpIfZero extends Instruction {
	int r;
	int a;
	
	JumpIfZero(int r, int a) {
		this.r = r;
		this.a = a;
	}
	
	void execute(Machine machine) {
		if (machine.registers[r] == 0)
			machine.pc = a;
		else
			machine.pc++;
	}
}

final class Jump extends Instruction {
	int a;
	
	Jump(int a) {
		this.a = a;
	}
	
	void execute(Machine machine) {
		machine.pc = a;
	}
}

final class Halt extends Instruction{

	void execute(Machine machine) {
		machine.pc = -1;
	}
}

final class Multiply extends Instruction {
	int r1;
	int r2;
	
	Multiply(int r1, int r2) {
		this.r1 = r1;
		this.r2 = r2;
	}
	
	void execute(Machine machine) {
		machine.registers[r1] *= machine.registers[r2];
		machine.pc++;
	}
}

final class Decrement extends Instruction {
	int r;
	
	Decrement(int r) {
		this.r = r;
	}
	
	void execute(Machine machine) {
		machine.registers[r]--;
		machine.pc++;
	}
}

class Machine {
	
	int pc;
	int[] registers;
	
	void execute(int[] registers, Instruction[] instructions) {
		this.registers = registers;
		while (0 <= pc) {
			Instruction instruction = instructions[pc];
			switch (instruction) {
			case LoadConstant l -> { registers[l.r] = l.c; }
			case Multiply m -> { registers[m.r1] *= registers[m.r2]; }
			case Decrement d -> { registers[d.r]--; }
			case JumpIfZero j -> { if (registers[j.r] == 0) pc = j.a; else pc++; }
			case Jump j -> { pc = j.a; }
			case Halt h -> { pc = -1; }
			// default -> throw new AssertionError();
			}
		}
	}
	
	int getCost(Instruction i) {
		return switch (i) {
		case LoadConstant c -> 1;
		case Multiply m -> 4;
		case JumpIfZero j -> 1;
		case Jump j -> 1;
		case Decrement d -> 1;
		case Halt h -> 1;
		};
	}
}

class MachineTest {

	@Test
	void test() {
		assertEquals(9, power(3, 2));
		assertEquals(128, power(2, 7));
	}
	
	int power(int x, int y) {
		Instruction[] program = {
				new LoadConstant(2, 1),
				new JumpIfZero(1, 5),
				new Multiply(2, 0),
				new Decrement(1),
				new Jump(1),
				new Halt()
		};
		int[] registers = {x, y, 0};
		new Machine().execute(registers, program);
		return registers[2];
	}

}
