package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;



public class Population {
	Individual[] indiLists;

	public Population() {
		indiLists = new Individual[Config.POPSIZE];
	}

	public Population(int size) {
		this.indiLists = new Individual[size];
	}

	public void sort() {
		Individual tmp;
		for (int i = 0; i < this.getSize() - 1; ++i) {
			for (int j = i + 1; j < this.getSize(); ++j) {
				if (indiLists[i].getFitness() > indiLists[j].getFitness()) {
					tmp = indiLists[i];
					indiLists[i] = indiLists[j];
					indiLists[j] = tmp;
				}
			}
		}
	}

	public Individual getIndividualByIndex(int index) {
		return indiLists[index];
	}

	public int getSize() {
		return this.indiLists.length;
	}
	
	public void addIndividual(Individual indi, int idx) {
		this.indiLists[idx] = indi;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
	
	public void initialize3() {
		Point endp = new Point(Config.W, Config.endY);
		for (int i = this.getSize() / 2; i < this.getSize(); ++i) {
			Random rand = new Random();
			Random rand1 = new Random();
			int sign;
			Individual indi = new Individual();
			int nbPoint = 0;
			double x = 0, y = Config.startY;
			double deltaX = 0, deltaY = 0;
			
			
			indi.addGene(new Point(x,y));
			++nbPoint;
			
			double distance = indi.getGene(nbPoint-1).distance(endp);
			while (distance > Config.len1){
				double ub;
				//if (i < 1000) {
					if (nbPoint > 2) {
						ub = Math.sin((indi.getGene(nbPoint-1).getY() - indi.getGene(nbPoint-2).getY())/Config.len1);
						//ub = Math.cos((indi.getGene(nbPoint-1).getX() - indi.getGene(nbPoint-2).getX())/Config.len);
					} else {
						ub = (rand1.nextDouble() * 2 - 1);
					}
//				} else {
//					ub = (rand.nextDouble() * 2 - 1);
//				}
				sign = (rand.nextDouble() * 2 - 1) < ub ? 1 : -1;
				if (Config.W - x  < Config.precise) {
					deltaX = Config.W - x;
				} else if (Config.W == x) {
					deltaX = 0;
				} else {
					deltaX = rand.nextDouble() * Config.len1;
				}
				
				deltaY = sign *  Math.sqrt(Config.len1 * Config.len1 - deltaX * deltaX);
				if (y + deltaY > Config.H || y + deltaY < 0) {
					deltaY = -deltaY;
				}
				
				if (x + deltaX > Config.W || y + deltaY > Config.H || y + deltaY < 0) {
					continue;
				} else {
					x += deltaX;
					y += deltaY;
					indi.addGene(new Point(x, y));
					++nbPoint;
					distance = indi.getGene(nbPoint-1).distance(endp);
				}
			}
			indi.addGene(endp);
			indi = indi.normalize();
			this.addIndividual(indi, i);
		}
	}
	
	public void initialize() {
		for (int i = 0; i < this.getSize() ; ++i) {
			Random rand = new Random();
			Random rand1 = new Random();
			Individual indi = new Individual();
			int nbPoint = 0;
			double x = 0;
			double y = Config.startY;
			double deltaX = 0, deltaY = 0;
			int sign;
			while (Config.W > x) {
				indi.addGene(new Point(x, y));
				++nbPoint;
				if (Config.W - x > Config.len1) {
					double ub;
					if (nbPoint > 2) {
						ub = Math.sin((indi.getGene(nbPoint-1).getY() - indi.getGene(nbPoint-2).getY())/Config.len1);
						//ub = Math.cos((indi.getGene(nbPoint-1).getX() - indi.getGene(nbPoint-2).getX())/Config.len);
					} else {
						ub = rand1.nextDouble();
					}
					sign = (rand.nextDouble() * 2 - 1) < ub ? 1 : -1;
					deltaX = rand.nextDouble() * Config.len1;
					
					deltaY = sign *  Math.sqrt(Config.len1 * Config.len1 - deltaX * deltaX);
					if (y + deltaY > Config.H || y + deltaY < 0) {
						deltaY = -deltaY;
					}
					
					x += deltaX;
					y += deltaY;
		
				} else {
					break;
				}
			}
			sign = rand.nextBoolean() ? 1 : -1;
			deltaX = Config.W - indi.getGene(nbPoint-1).getX();
			deltaY = sign * Math.sqrt(Config.len1 * Config.len1 - deltaX * deltaX);
			if (y + deltaY > Config.H || y + deltaY < 0) {
				deltaY = -deltaY;
			}
			x = Config.W;
			y = indi.getGene(nbPoint-1).getY() + deltaY;
			indi.addGene(new Point(x, y)); 
			++nbPoint;
			
			//along boundary
			deltaY = y - Config.endY;
			while(Math.abs(deltaY) > Config.len1) {
				if (deltaY < 0) {
					y += Config.len1;
					deltaY += Config.len1;
				} else {
					y -= Config.len1;
					deltaY -= Config.len1;
				}
				indi.addGene(new Point(Config.W, y));
				++nbPoint;
			}
			
			if (deltaY != 0) {
				indi.addGene(new Point(Config.W, Config.endY));
			}
			indi = indi.normalize();
			this.addIndividual(indi, i);
			
		}
	}
	
	public void initializeRandomStart() {
		for (int i = 0; i < this.getSize() ; ++i) {
			Random rand = new Random();
			Random rand1 = new Random();
			Individual indi = new Individual();
			int nbPoint = 0;
			double x = 0;
			double y = Config.startY;
			double deltaX = 0, deltaY = 0;
			int sign;
			double firstY = rand.nextInt(501);
			while (firstY > y && (firstY - y >= Config.len1)) {
				indi.addGene(new Point(x, y));
				nbPoint++;
				y+= Config.len1;
			}
			while (firstY < y && (y - firstY >= Config.len1)) {
				indi.addGene(new Point(x, y));
				nbPoint++;
				y-= Config.len1;
			}
			while (Config.W > x) {
				indi.addGene(new Point(x, y));
				++nbPoint;
				if (Config.W - x > Config.len1) {
					double ub;
					if (nbPoint > 2) {
						ub = Math.sin((indi.getGene(nbPoint-1).getY() - indi.getGene(nbPoint-2).getY())/Config.len1);
						//ub = Math.cos((indi.getGene(nbPoint-1).getX() - indi.getGene(nbPoint-2).getX())/Config.len);
					} else {
						ub = rand1.nextDouble();
					}
					sign = (rand.nextDouble() * 2 - 1) < ub ? 1 : -1;
					deltaX = rand.nextDouble() * Config.len1;
					
					deltaY = sign *  Math.sqrt(Config.len1 * Config.len1 - deltaX * deltaX);
					if (y + deltaY > Config.H || y + deltaY < 0) {
						deltaY = -deltaY;
					}
					
					x += deltaX;
					y += deltaY;
		
				} else {
					break;
				}
			}
			sign = rand.nextBoolean() ? 1 : -1;
			deltaX = Config.W - indi.getGene(nbPoint-1).getX();
			deltaY = sign * Math.sqrt(Config.len1 * Config.len1 - deltaX * deltaX);
			if (y + deltaY > Config.H || y + deltaY < 0) {
				deltaY = -deltaY;
			}
			x = Config.W;
			y = indi.getGene(nbPoint-1).getY() + deltaY;
			indi.addGene(new Point(x, y)); 
			++nbPoint;
			
			//along boundary
			deltaY = y - Config.endY;
			while(Math.abs(deltaY) > Config.len1) {
				if (deltaY < 0) {
					y += Config.len1;
					deltaY += Config.len1;
				} else {
					y -= Config.len1;
					deltaY -= Config.len1;
				}
				indi.addGene(new Point(Config.W, y));
				++nbPoint;
			}
			
			if (deltaY != 0) {
				indi.addGene(new Point(Config.W, Config.endY));
			}
			indi = indi.normalize();
			this.addIndividual(indi, i);
			
		}
	}
	
	public void calFitness(Topo topo) {
		for (int i = 0; i < this.getSize(); ++i) {
			this.getIndividualByIndex(i).calFitness(topo);
		}
	}
	
	public Population mergeSort(Population pOffs) {
		Population tmp = new Population();
		int i1 = 0, i2 = 0;
		int nbC = 0;
		while (i1 < this.getSize() && i2 < pOffs.getSize() && nbC < Config.POPSIZE) {
			if (this.getIndividualByIndex(i1).getFitness() < pOffs.getIndividualByIndex(i2).getFitness()) {
				tmp.addIndividual(this.getIndividualByIndex(i1), nbC);
				++i1;
			} else {
				tmp.addIndividual(pOffs.getIndividualByIndex(i2), nbC);
				++i2;
			}
			++nbC;
		}
		if (nbC != tmp.getSize()) {
			if (i1 == this.getSize()) {
				for (; nbC < tmp.getSize(); ++nbC, ++i2) {
					tmp.addIndividual(pOffs.getIndividualByIndex(i2), nbC);
				}
			} else {
				for (; nbC < tmp.getSize(); ++nbC, ++i1) {
					tmp.addIndividual(this.getIndividualByIndex(i1), nbC);
				}
			}
		}
		return tmp;
	}
	
	public Population evolve(Topo topo) throws CloneNotSupportedException{
		Random rand = new Random();
		int nbCross = (int) (Config.POPSIZE * Config.P_CROSSOVER);
		int nbMutate = (int) (Config.POPSIZE * Config.P_MUTATION);
		int nbCross1 = (int)(nbCross * Config.P_CROSSOVER1);
		Population p = new Population(nbCross + nbMutate);
		Point endp = new Point(Config.W, Config.endY);
		int idx1, idx2 = 0;
		Individual female, male = null;
		
		for (int i = 0; i < nbCross1; ++i){
			idx1 = rand.nextInt(Config.POPSIZE);
			female = this.getIndividualByIndex(idx1);
			while (idx2 == idx1) {
				idx2 = rand.nextInt(Config.POPSIZE);
			}
			try {
				male = this.getIndividualByIndex(idx2);
			} catch (Exception e) {
				System.out.println("male null");
			}
			
			/*double df = -1, minDf = Double.POSITIVE_INFINITY;
			
			for (int j = 0; j < 5; ++j) {
				idx2 = rand.nextInt(Config.POPSIZE);
				while (idx2 == idx1) {
					idx2 = rand.nextInt(Config.POPSIZE);
				}
				
				df = this.getIndividualByIndex(idx2).differ(female);
				if (df < minDf){
					minDf = df;
					male = this.getIndividualByIndex(idx2);
				}
			}*/
			
			//cross
			Individual child1 = new Individual();
			Individual child2 = new Individual();
			// cross point
			int idx = 1 + rand.nextInt(Math.min(female.getLength(), male.getLength()) - 2);
			int id;
			for (id = 0;  id <= idx; ++id) {
				child1.addGene(female.getGene(id));
				child2.addGene(male.getGene(id));
			}
			
			boolean find = false;
			idx1 = idx;
			idx2 = idx;
			//child1
			while(true) {
				find = false;
				for (id = idx1 + 1; id < male.getLength(); ++id) {
					if (Double.compare(male.getGene(id).getX(), child1.getLastGene().getX()) < 0) {
						continue;
					} 
					
					if (Double.compare(male.getGene(id).distance(child1.getLastGene()), Config.len) < 0) {
						continue;
					}
					idx1 = id;
					find = true;
					break;
				}
				
				if (find) {
					double deltaX, deltaY;
					if (Double.compare(male.getGene(idx1).getX(), child1.getLastGene().getX()) == 0) {
						deltaX = 0;
						deltaY = Config.len * (child1.getLastGene().getY() < male.getGene(idx1).getY() ? 1 : -1);
					} else {
						double phi = Math.atan((male.getGene(idx1).getY() - child1.getLastGene().getY())
								/ (male.getGene(idx1).getX() - child1.getLastGene().getX()));
						deltaX = Config.len * Math.cos(phi);
						deltaY = Config.len * Math.sin(phi);
						if (Double.compare(deltaX, Config.len) == 0) {
							deltaY = 0;
						} 
						if (Double.compare(Math.abs(deltaY), Config.len) == 0) {
							deltaX = 0;
						}
						
					}
					Point po = new Point(child1.getLastGene().getX() + deltaX, child1.getLastGene().getY() + deltaY);
					child1.addGene(po);
				} else {
					double deltaX, deltaY;
					int nbp = (int)Math.floor(child1.getLastGene().distance(endp) / Config.len);
					if (Double.compare(endp.getX(), child1.getLastGene().getX()) == 0) {
						deltaY = Config.len * ((endp.getY() - child1.getLastGene().getY()) < 0 ? -1 : 1);
						for (int ip = 0; ip < nbp; ++ip) {
							child1.addGene(new Point(endp.getX(), child1.getLastGene().getY() + deltaY));
						}
					} else {
						double phi = Math.atan((endp.getY() - child1.getLastGene().getY()) / (endp.getX() - child1.getLastGene().getX()));
						deltaX = Config.len * Math.cos(phi);
						deltaY = Config.len * Math.sin(phi);
						if (Double.compare(deltaX, Config.len) == 0) {
							deltaY = 0;
						} 
						if (Double.compare(Math.abs(deltaY), Config.len) == 0) {
							deltaX = 0;
						}
						for (int ip = 0; ip < nbp; ++ip) {
							child1.addGene(new Point(child1.getLastGene().getX() + deltaX, child1.getLastGene().getY() + deltaY));
						}
					}
					if (Double.compare(child1.getLastGene().distance(endp), 0.0) != 0) {
						child1.addGene(endp);
					}
					break;
				}
			}
			
			//child2
			while(true) {
				find = false;
				for (id = idx2 + 1; id < female.getLength(); ++id) {
					if (Double.compare(female.getGene(id).getX(), child2.getLastGene().getX()) < 0) {
						continue;
					} 
					
					if (Double.compare(female.getGene(id).distance(child2.getLastGene()), Config.len) < 0) {
						continue;
					}
					idx2 = id;
					find = true;
					break;
				}
				
				if (find) {
					double deltaX, deltaY;
					if (Double.compare(female.getGene(idx2).getX(), child2.getLastGene().getX()) == 0) {
						deltaX = 0;
						deltaY = Config.len * (child2.getLastGene().getY() < female.getGene(idx2).getY() ? 1 : -1);
					} else {
						double phi = Math.atan((female.getGene(idx2).getY() - child2.getLastGene().getY())
								/ (female.getGene(idx2).getX() - child2.getLastGene().getX()));
						deltaX = Config.len * Math.cos(phi);
						deltaY = Config.len * Math.sin(phi);
						if (Double.compare(deltaX, Config.len) == 0) {
							deltaY = 0;
						} 
						if (Double.compare(Math.abs(deltaY), Config.len) == 0) {
							deltaX = 0;
						}
					}
					Point po = new Point(child2.getLastGene().getX() + deltaX, child2.getLastGene().getY() + deltaY);
					child2.addGene(po);
				} else {
					double deltaX, deltaY;
					int nbp = (int)Math.floor(child2.getLastGene().distance(endp) / Config.len);
					if (Double.compare(endp.getX(), child2.getLastGene().getX()) == 0) {
						deltaY = Config.len * ((endp.getY() - child2.getLastGene().getY()) < 0 ? -1 : 1);
						for (int ip = 0; ip < nbp; ++ip) {
							child2.addGene(new Point(endp.getX(), child2.getLastGene().getY() + deltaY));
						}
					} else {
						double phi = Math.atan((endp.getY() - child2.getLastGene().getY()) / (endp.getX() - child2.getLastGene().getX()));
						deltaX = Config.len * Math.cos(phi);
						deltaY = Config.len * Math.sin(phi);
						if (Double.compare(deltaX, Config.len) == 0) {
							deltaY = 0;
						} 
						if (Double.compare(Math.abs(deltaY), Config.len) == 0) {
							deltaX = 0;
						}
						for (int ip = 0; ip < nbp; ++ip) {
							child2.addGene(new Point(child2.getLastGene().getX() + deltaX, child2.getLastGene().getY() + deltaY));
						}
					}
					if (Double.compare(child2.getLastGene().distance(endp), 0.0) != 0) {
						child2.addGene(endp);
					}
					break;
				}
			}
//			if (!child1.isValid()) {
//				System.out.println("lai ghep con 1");
//			}
//			if (!child2.isValid()) {
//				System.out.println("lai ghep con 2");
//			}
			child1.calFitness(topo);
			child2.calFitness(topo);
			p.addIndividual(child1, i);
			p.addIndividual(child2, ++i);
		}
		
		for (int i = nbCross1; i < nbCross; ++i) {
			idx1 = rand.nextInt(Config.POPSIZE + nbCross1);
			if (idx1 < Config.POPSIZE){
				female = this.getIndividualByIndex(idx1);
			} else {
				female = p.getIndividualByIndex(idx1 - Config.POPSIZE);
			}
			double df = -1, maxDf = 0, minDf = Double.POSITIVE_INFINITY;
			
			for (int j = 0; j < 5; ++j) {
				idx2 = rand.nextInt(Config.POPSIZE);
				while (idx2 == idx1) {
					idx2 = rand.nextInt(Config.POPSIZE);
				}
				
				df = this.getIndividualByIndex(idx2).differ(female);
				
				if (df < minDf){
					minDf = df;
					male = this.getIndividualByIndex(rand.nextInt(Config.POPSIZE));
				}
			}
			
			//cross
			Individual child = new Individual();
			child.addGene(new Point(0, Config.startY));
			idx1 = 1; idx2 = 1;
			boolean find1, find2;
			double phi1, phi2, phi, maxPhi, minPhi;
			double deltaX, deltaY, x = 0, y = Config.startY;
			while (true) {
				find1 = false; find2 = false;
				if (Double.compare(child.getLastGene().getX(), endp.getX()) == 0) {
					int nbp = (int)Math.floor(child.getLastGene().distance(endp) / Config.len);
					deltaY = Config.len * ((endp.getY() - child.getLastGene().getY()) < 0 ? -1 : 1);
					for (int ip = 0; ip < nbp; ++ip) {
						child.addGene(new Point(endp.getX(), child.getLastGene().getY() + deltaY));
					}
					if (Double.compare(child.getLastGene().distance(endp), 0.0) != 0) {
						child.addGene(endp);
					}
					break;
				}
				//find valid point in female
				for (int id = idx1; id < female.getLength(); ++id) {
					if (female.getGene(id).getX() < child.getLastGene().getX()) {
						continue;
					}
					if (child.getLastGene().distance(female.getGene(id)) < Config.len) {
						continue;
					}
					find1 = true;
					idx1 = id;
					break;
				}
				
				//find valid point in male
				for (int id = idx2; id < male.getLength(); ++id) {
					if (male.getGene(id).getX() < child.getLastGene().getX()) {
						continue;
					}
					if (child.getLastGene().distance(male.getGene(id)) < Config.len) {
						continue;
					}
					find2 = true;
					idx2 = id;
					break;
				}
				
				if (find1 && find2) {//both female and male have valid point
					if (Double.compare(female.getGene(idx1).getX(), child.getLastGene().getX()) == 0) {
						phi1 = 0.5 * Math.PI * ((female.getGene(idx1).getY() - child.getLastGene().getY()) < 0 ? -1 : 1);
					} else {
						phi1 = Math.atan((female.getGene(idx1).getY() - child.getLastGene().getY())
								/(female.getGene(idx1).getX() - child.getLastGene().getX()));
					}
					if (Double.compare(male.getGene(idx2).getX(), child.getLastGene().getX()) == 0) {
						phi2 = 0.5 * Math.PI * ((male.getGene(idx2).getY() - child.getLastGene().getY()) < 0 ? -1 : 1);
					} else {
						phi2 = Math.atan((male.getGene(idx2).getY() - child.getLastGene().getY())
								/(male.getGene(idx2).getX() - child.getLastGene().getX()));
					}
					maxPhi = Math.max(phi1, phi2);
					minPhi = Math.min(phi1, phi2);
//					double lb = Math.max(-Math.PI/2, minPhi - Math.abs(phi1 - phi2) * Config.PBX_alpha);
//					double ub = Math.min(Math.PI/2, maxPhi + Math.abs(phi1 - phi2) * Config.PBX_alpha);
					double lb = minPhi - Math.abs(phi1 - phi2) * Config.PBX_alpha;
					double ub = maxPhi + Math.abs(phi1 - phi2) * Config.PBX_alpha;
					phi = lb + rand.nextDouble() * (ub - lb);
					if (Double.compare(phi, -Math.PI/2) < 0) {
						phi = -Math.PI / 2;
					} else if (Double.compare(phi, Math.PI/2) > 0) {
						phi = Math.PI / 2;
					}
					if (child.getLastGene().getX() + Config.len * Math.cos(phi) > Config.W) {
						deltaX = Config.W - child.getLastGene().getX();
						deltaY = Math.sqrt(Config.len * Config.len - deltaX * deltaX) 
								* (child.getLastGene().getY() < endp.getY() ? 1 : -1);
					} else {
						if (child.getLastGene().getY() + Config.len * Math.sin(phi) < 0) {
							deltaY = 0 - child.getLastGene().getY();
							deltaX = Math.sqrt(Config.len * Config.len - deltaY * deltaY);
						} else if (child.getLastGene().getY() + Config.len * Math.sin(phi) > Config.H) {
							deltaY = Config.H - child.getLastGene().getY();
							deltaX = Math.sqrt(Config.len * Config.len - deltaY * deltaY);
						} else {
							deltaY = Config.len * Math.sin(phi);
							deltaX = Config.len * Math.cos(phi);
						}
					}
					x += deltaX;
					y += deltaY;
					child.addGene(new Point(x, y));
				} else if (find1) {//only valid point on female found
					if (Double.compare(female.getGene(idx1).getX(), child.getLastGene().getX()) == 0) {
						deltaX = 0;
						deltaY = Config.len * ((female.getGene(idx1).getY() - child.getLastGene().getY()) < 0 ? -1 : 1);
					} else {
						phi1 =  Math.atan((female.getGene(idx1).getY() - child.getLastGene().getY())
								/(female.getGene(idx1).getX() - child.getLastGene().getX()));
						deltaY = Config.len * Math.sin(phi1);
						deltaX = Config.len * Math.cos(phi1);
					}
					x += deltaX;
					y += deltaY;
					child.addGene(new Point(x, y));
				} else if (find2) {//only valid point on male found
					if (Double.compare(male.getGene(idx2).getX(), child.getLastGene().getX()) == 0) {
						deltaX = 0;
						deltaY = Config.len * ((male.getGene(idx2).getY() - child.getLastGene().getY()) < 0 ? -1 : 1);
					} else {
						phi2 =  Math.atan((male.getGene(idx2).getY() - child.getLastGene().getY())
								/(male.getGene(idx2).getX() - child.getLastGene().getX()));
						deltaY = Config.len * Math.sin(phi2);
						deltaX = Config.len * Math.cos(phi2);
					}
					x += deltaX;
					y += deltaY;
					child.addGene(new Point(x, y));
				} else {
					child.addGene(endp);
					break;
				}
			}
//			if (!child.isValid()) {
//				System.out.println("lai ghep 2");
//			}
			child.calFitness(topo);
			p.addIndividual(child, i);
		}
		
		
		for (int i = 0; i < nbMutate; ++i) {
			idx1 = rand.nextInt(Config.POPSIZE + nbCross);
			Individual child = new Individual();
			Individual parent;
			boolean isValid = true;
			if (idx1 < Config.POPSIZE) {
				parent = (Individual) this.getIndividualByIndex(idx1);
			} else {
				parent = (Individual) p.getIndividualByIndex(idx1 - Config.POPSIZE);
			}
			
			// find the worst point
			double wpt = -1;
			for (int j = 1; j < parent.getLength()-1; ++j) {
				if (parent.getGene(j).getProb() > wpt) {
					wpt = parent.getGene(j).getProb();
					idx2 = j;
				}
			}
			
			for (int j = 0; j < idx2; ++j) {
				child.addGene(parent.getGene(j));
			}
			
			double deltaX, deltaY;
			while(true) {
				if (child.getLastGene().getX() > Config.W || child.getLastGene().getY() > Config.H
						|| child.getLastGene().getY() < 0) {
					System.out.println(child.getLastGene().getX() + " --- "+ child.getLastGene().getY());
				}
				//find valid point
				boolean find = false;
				if (Double.compare(child.getLastGene().getX(), parent.getGene(idx2).getX()) == 0) {
					for (int j = idx2 + 1; j < parent.getLength(); ++j) {
						if (parent.getGene(j).getX() > child.getLastGene().getX() 
								&& child.getLastGene().distance(parent.getGene(j)) >= Config.len) {
							find = true;
							idx2 = j;
						}
					}
				} else {
					double phi = Math.atan((parent.getGene(idx2).getY() - child.getLastGene().getY()) / 
								(parent.getGene(idx2).getX() - child.getLastGene().getX()));
					for (int j = idx2 + 1; j < parent.getLength(); ++j) {
						if (child.getLastGene().distance(parent.getGene(j)) >= Config.len) {
							double phi1 = Math.atan((parent.getGene(j).getY() - child.getLastGene().getY()) / 
									(parent.getGene(j).getX() - child.getLastGene().getX()));
							if (Double.compare(phi1, phi) != 0) {
								find = true;
								idx2 = j;
							}
						}
					}
				}
				
				if (find) {
					double phi = Math.atan((parent.getGene(idx2).getY() - child.getLastGene().getY()) / 
							(parent.getGene(idx2).getX() - child.getLastGene().getX()));
					deltaX = Config.len * Math.cos(phi);
					deltaY = Config.len * Math.sin(phi);
					if (Double.compare(Math.abs(deltaY), Config.len) == 0) {
						deltaX = 0;
					} else if (Double.compare(deltaX, Config.len) == 0) {
						deltaY = 0;
					}
					child.addGene(new Point(child.getLastGene().getX() + deltaX, child.getLastGene().getY() + deltaY));
				} else {
					if (child.getLastGene().distance(endp) < Config.len) {
						child.addGene(endp);
						break;
					}
					idx2 = parent.getLength() - 1;
					if (Double.compare(endp.getX(), child.getLastGene().getX()) == 0) {
						deltaX = 0;
						deltaY = Config.len * ((endp.getY() - child.getLastGene().getY()) < 0 ? -1 : 1);
					} else {
						double phi =  Math.atan((endp.getY() - child.getLastGene().getY())
								/(endp.getX() - child.getLastGene().getX()));
						deltaY = Config.len * Math.sin(phi);
						deltaX = Config.len * Math.cos(phi);
						if (Double.compare(Math.abs(deltaY), Config.len) == 0) {
							deltaX = 0;
						} 
						if (Double.compare(deltaX, Config.len) == 0) {
							deltaY = 0;
						}
					}
					 
					child.addGene(new Point(child.getLastGene().getX() + deltaX, child.getLastGene().getY() + deltaY));
					
				}
			}
			
			child.calFitness(topo);
			p.addIndividual(child, nbCross + i);
		}
		return p;
	}
	
}
