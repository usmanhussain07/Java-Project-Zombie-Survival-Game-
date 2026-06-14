/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject38;

/**
 *
 * @author HP
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class ZombieSurvivalGame {
    public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> { 
            JFrame frame = new JFrame("Zombie Survival — OOP Game"); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            frame.setResizable(false); 
            GamePanel gp = new GamePanel(); 
            frame.add(gp); 
            frame.pack(); 
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true); 
            gp.requestFocusInWindow(); 
        }); 
    } 
}

abstract class Entity {
    protected int x, y; 
    protected int w, h; 
    protected int hp, maxHp; 
    protected boolean alive; 
    
    public Entity(int x, int y, int w, int h, int hp) { 
        this.x = x; 
        this.y = y; 
        this.w = w; 
        this.h = h; 
        this.hp = this.maxHp = hp; 
        this.alive = true; 
    } 
    
    public Rectangle bounds() { 
        return new Rectangle(x, y, w, h); 
    } 
    
    public boolean isAlive() { 
        return alive && hp > 0; 
    } 
    
    public void takeDamage(int dmg) { 
        hp -= dmg; 
        if (hp <= 0) { 
            hp = 0; 
            alive = false; 
        } 
    } 
    
    public abstract void draw(Graphics2D g); 
}

class Player extends Entity {
    private int speed = 4; 
    private int damage = 35; 
    private int score = 0; 
    private int kills = 0; 
    private int level = 1; 
    private int xp = 0; 
    private int xpNeeded = 8; 
    private float anim = 0; 
    private long lastShot = 0; 
    private int shotDelay = 300; 
    private int aimX = 400, aimY = 300; 
    boolean goUp, goDown, goLeft, goRight; 
    
    public Player(int x, int y) { 
        super(x, y, 36, 36, 150); 
    } 
    
    public void update(int mapW, int mapH) { 
        if (goUp) y = Math.max(15, y - speed); 
        if (goDown) y = Math.min(mapH - h - 15, y + speed); 
        if (goLeft) x = Math.max(15, x - speed); 
        if (goRight) x = Math.min(mapW - w - 15, x + speed); 
        anim += 0.2f; 
    } 
    
    public boolean readyToShoot() { 
        return System.currentTimeMillis() - lastShot >= shotDelay; 
    } 
    
    public void markShot() { 
        lastShot = System.currentTimeMillis(); 
    } 
    
    public void addXP(int amount) { 
        xp += amount; 
        if (xp >= xpNeeded) { 
            xp -= xpNeeded; 
            level++; 
            xpNeeded = level * 10; 
            damage += 8; 
            maxHp += 20; 
            hp = Math.min(hp + 40, maxHp); 
            speed = Math.min(speed + 1, 8); 
            shotDelay = Math.max(150, shotDelay - 20); 
        } 
    } 
    
    public void setAim(int ax, int ay) { aimX = ax; aimY = ay; } 
    public int getAimX() { return aimX; } 
    public int getAimY() { return aimY; } 
    public int getScore() { return score; } 
    public int getKills() { return kills; } 
    public int getLevel() { return level; } 
    public int getXP() { return xp; } 
    public int getXPMax() { return xpNeeded; } 
    public int getDmg() { return damage; } 
    public void addScore(int s) { score += s; } 
    public void addKill() { kills++; } 
    
    @Override 
    public void draw(Graphics2D g) { 
        int bob = (int)(Math.sin(anim) * 2); 
        g.setColor(new Color(0, 0, 0, 50)); 
        g.fillOval(x + 5, y + h - 2, w - 10, 8); 
        g.setColor(new Color(30, 50, 100)); 
        int legBob = (int)(Math.sin(anim * 2) * 3); 
        g.fillRoundRect(x + 6, y + 25 + bob + legBob, 10, 12, 4, 4); 
        g.fillRoundRect(x + 20, y + 25 + bob - legBob, 10, 12, 4, 4); 
        g.setColor(new Color(20, 120, 240)); 
        g.fillRoundRect(x + 5, y + 11 + bob, w - 10, 16, 8, 8); 
        g.setColor(new Color(60, 40, 10)); 
        g.fillRect(x + 5, y + 22 + bob, w - 10, 4); 
        g.setColor(new Color(210, 170, 30)); 
        g.fillRect(x + w/2 - 3, y + 22 + bob, 6, 4); 
        g.setColor(new Color(255, 210, 165)); 
        g.fillOval(x + 7, y + bob, w - 14, 19); 
        g.setColor(new Color(25, 25, 25)); 
        g.fillArc(x + 7, y - 2 + bob, w - 14, 15, 0, 180); 
        g.setColor(Color.WHITE); 
        g.fillOval(x + 10, y + 5 + bob, 6, 5); 
        g.fillOval(x + 21, y + 5 + bob, 6, 5); 
        g.setColor(new Color(0, 80, 230)); 
        g.fillOval(x + 12, y + 6 + bob, 3, 3); 
        g.fillOval(x + 23, y + 6 + bob, 3, 3); 
        drawGun(g, bob); 
        drawBar(g, x - 2, y - 13, w + 4, 7, (float) hp / maxHp, hp > maxHp/2 ? new Color(0,210,75) : hp > maxHp/4 ? new Color(255,160,0) : Color.RED); 
    } 
    
    private void drawGun(Graphics2D g, int bob) { 
        int cx = x + w / 2; 
        int cy = y + h / 2 + bob; 
        double angle = Math.atan2(aimY - cy, aimX - cx); 
        Graphics2D g2 = (Graphics2D) g.create(); 
        g2.translate(cx, cy); 
        g2.rotate(angle); 
        g2.setColor(new Color(45, 45, 45)); 
        g2.fillRoundRect(2, -4, 22, 8, 3, 3); 
        g2.setColor(new Color(75, 50, 20)); 
        g2.fillRoundRect(3, 3, 10, 7, 2, 2); 
        g2.setColor(new Color(110, 110, 110)); 
        g2.fillRect(20, -3, 5, 6); 
        g2.dispose(); 
    } 
    
    private void drawBar(Graphics2D g, int bx, int by, int bw, int bh, float ratio, Color fill) { 
        g.setColor(new Color(50, 0, 0)); 
        g.fillRoundRect(bx, by, bw, bh, 4, 4); 
        g.setColor(fill); 
        g.fillRoundRect(bx, by, (int)(bw * ratio), bh, 4, 4); 
        g.setColor(new Color(255, 255, 255, 70)); 
        g.drawRoundRect(bx, by, bw, bh, 4, 4); 
    } 
}

abstract class Zombie extends Entity {
    protected int spd; 
    protected int dmg; 
    protected int xpGive; 
    protected int scoreGive; 
    protected float anim = 0; 
    protected long lastBite = 0; 
    protected int biteCool = 600; 
    
    public Zombie(int x, int y, int w, int h, int hp, int spd, int dmg, int xp, int sc) { 
        super(x, y, w, h, hp); 
        this.spd = spd; 
        this.dmg = dmg; 
        this.xpGive = xp; 
        this.scoreGive = sc; 
    } 
    
    protected float fx, fy; 
    protected boolean fInit = false; 
    
    public void update(int px, int py) { 
        if (!alive) return; 
        if (!fInit) { 
            fx = x; 
            fy = y; 
            fInit = true; 
        } 
        double dx = px - (fx + w/2); 
        double dy = py - (fy + h/2); 
        double dist = Math.sqrt(dx*dx + dy*dy); 
        if (dist > 1) { 
            fx += (float)(dx / dist * spd); 
            fy += (float)(dy / dist * spd); 
        } 
        x = (int)fx; 
        y = (int)fy; 
        anim += 0.15f; 
    } 
    
    public void separateFrom(Zombie other) { 
        double dx = (x + w/2) - (other.x + other.w/2); 
        double dy = (y + h/2) - (other.y + other.h/2); 
        double dist = Math.sqrt(dx*dx + dy*dy); 
        double minDist = (w + other.w) / 2.0 + 2; 
        if (dist < minDist && dist > 0.1) { 
            double push = (minDist - dist) / 2.0; 
            x += (int)(dx / dist * push); 
            y += (int)(dy / dist * push); 
        } 
    } 
    
    public boolean canBite(Player p) { 
        return bounds().intersects(p.bounds()) && System.currentTimeMillis() - lastBite >= biteCool; 
    } 
    
    public void bite(Player p) { 
        p.takeDamage(dmg); 
        lastBite = System.currentTimeMillis(); 
    } 
    
    public int getXP() { return xpGive; } 
    public int getScore() { return scoreGive; } 
    
    protected void hpBar(Graphics2D g, Color c) { 
        int bw = w + 8; 
        g.setColor(new Color(40, 0, 0)); 
        g.fillRoundRect(x - 4, y - 10, bw, 5, 3, 3); 
        g.setColor(c); 
        g.fillRoundRect(x - 4, y - 10, (int)(bw * (float)hp/maxHp), 5, 3, 3); 
    } 
}

class NormalZombie extends Zombie {
    public NormalZombie(int x, int y) { 
        super(x, y, 32, 32, 45, 2, 18, 5, 100); 
    } 
    
    @Override 
    public void draw(Graphics2D g) { 
        int bob = (int)(Math.sin(anim) * 3); 
        g.setColor(new Color(0,0,0,40)); 
        g.fillOval(x+4, y+h-3, w-8, 8); 
        g.setColor(new Color(50, 105, 50)); 
        g.fillRoundRect(x+4, y+12+bob, w-8, h-14, 8, 8); 
        g.setColor(new Color(35, 75, 35)); 
        g.fillRect(x+6, y+15+bob, 8, 5); 
        g.setColor(new Color(125, 170, 85)); 
        g.fillOval(x+5, y+bob, w-10, 18); 
        g.setColor(new Color(160, 10, 10, 140)); 
        g.fillOval(x+8, y+5+bob, 4, 3); 
        g.drawLine(x+12, y+2+bob, x+15, y+7+bob); 
        g.setColor(new Color(255, 30, 0)); 
        g.fillOval(x+7, y+5+bob, 6, 5); 
        g.fillOval(x+18, y+5+bob, 6, 5); 
        g.setColor(new Color(255, 140, 100)); 
        g.fillOval(x+9, y+6+bob, 3, 3); 
        g.fillOval(x+20, y+6+bob, 3, 3); 
        g.setColor(new Color(80, 130, 60)); 
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
        g.drawLine(x+4, y+14+bob, x-10, y+22+bob); 
        g.drawLine(x+w-4, y+14+bob, x+w+10, y+22+bob); 
        g.setStroke(new BasicStroke(1)); 
        hpBar(g, new Color(200, 30, 30)); 
    } 
}

class FastZombie extends Zombie {
    public FastZombie(int x, int y) { 
        super(x, y, 28, 28, 65, 4, 22, 15, 250); 
    } 
    
    @Override 
    public void draw(Graphics2D g) { 
        int bob = (int)(Math.sin(anim * 2) * 4); 
        g.setColor(new Color(0,0,0,35)); 
        g.fillOval(x+3, y+h-3, w-6, 7); 
        
        g.setColor(new Color(110, 30, 140)); 
        g.fillRoundRect(x+3, y+10+bob, w-6, h-12, 6, 6); 
        g.setColor(new Color(155, 55, 195)); 
        g.fillOval(x+3, y+bob, w-6, 16); 
        
        g.setColor(new Color(0, 255, 230)); 
        g.fillOval(x+5, y+4+bob, 5, 4); 
        g.fillOval(x+16, y+4+bob, 5, 4); 
        
        g.setColor(new Color(180, 70, 255, 110)); 
        g.setStroke(new BasicStroke(1.5f)); 
        for (int i = 0; i < 3; i++) g.drawLine(x-5-i*4, y+h/2-i, x-13-i*4, y+h/2-i); 
        g.setStroke(new BasicStroke(1)); 
        hpBar(g, new Color(180, 30, 250)); 
    } 
}

class BossZombie extends Zombie {
    public BossZombie(int x, int y) { 
        super(x, y, 64, 64, 500, 2, 35, 60, 2000); 
        biteCool = 1200; 
    } 
    
    @Override 
    public void draw(Graphics2D g) { 
        int bob = (int)(Math.sin(anim * 0.6f) * 2); 
        g.setColor(new Color(120, 0, 0, 30)); 
        g.fillOval(x-14, y-14+bob, w+28, h+28); 
        g.setColor(new Color(0,0,0,60)); 
        g.fillOval(x+5, y+h-3, w-10, 11); 
        
        g.setColor(new Color(70, 15, 15)); 
        g.fillRoundRect(x+4, y+20+bob, w-8, h-22, 12, 12); 
        g.setColor(new Color(35, 5, 5)); 
        g.fillRect(x+8, y+24+bob, 16, 10); 
        g.fillRect(x+38, y+24+bob, 16, 10); 
        
        g.setColor(new Color(110, 25, 25)); 
        g.fillOval(x+6, y+bob, w-12, 32); 
        
        g.setColor(new Color(255, 185, 0)); 
        int[] bx2 = {x+16, x+24, x+32, x+40, x+48}; int[] by2 = {y+8, y+2, y+5, y+2, y+8}; 
        for (int i = 0; i < 5; i++) g.fillPolygon(new int[]{bx2[i]-4,bx2[i],bx2[i]+4}, new int[]{by2[i]+6+bob,by2[i]+bob,by2[i]+6+bob}, 3); 
        
        g.setColor(new Color(255, 60, 0)); 
        g.fillOval(x+15, y+12+bob, 12, 9); 
        g.fillOval(x+37, y+12+bob, 12, 9); 
        g.setColor(Color.BLACK); 
        g.fillOval(x+19, y+14+bob, 5, 5); 
        g.fillOval(x+41, y+14+bob, 5, 5); 
        
        g.setColor(new Color(90, 20, 20)); 
        g.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
        g.drawLine(x+6, y+26+bob, x-20, y+42+bob); 
        g.drawLine(x+w-6, y+26+bob, x+w+20, y+42+bob); 
        g.setStroke(new BasicStroke(1)); 
        
        g.setFont(new Font("Arial Black", Font.BOLD, 11)); 
        g.setColor(new Color(255, 50, 50)); 
        FontMetrics fm = g.getFontMetrics(); 
        String lbl = "ELITE BOSS"; 
        g.drawString(lbl, x + w/2 - fm.stringWidth(lbl)/2, y - 18 + bob); 
        
        int bw2 = w + 16; 
        g.setColor(new Color(50,0,0)); 
        g.fillRoundRect(x-8, y-13, bw2, 7, 4, 4); 
        float r = (float)hp/maxHp; 
        g.setColor(r > 0.5f ? new Color(255,30,0) : new Color(180,0,0)); 
        g.fillRoundRect(x-8, y-13, (int)(bw2*r), 7, 4, 4); 
        g.setColor(new Color(255,255,255,90)); 
        g.drawRoundRect(x-8, y-13, bw2, 7, 4, 4); 
    } 
}

class Bullet extends Entity {
    private final double vx; 
    private final double vy; 
    private final int damage; 
    private int traveled = 0; 
    
    public Bullet(int sx, int sy, int tx, int ty, int dmg) { 
        super(sx, sy, 9, 9, 1); 
        this.damage = dmg; 
        double dx = tx - sx, dy = ty - sy; 
        double d = Math.sqrt(dx*dx + dy*dy); 
        if (d == 0) d = 1; 
        vx = dx / d * 13; 
        vy = dy / d * 13; 
    } 
    
    public void update() { 
        x += vx; 
        y += vy; 
        traveled += 13; 
        if (traveled > 650) alive = false; 
    } 
    
    public int getDmg() { return damage; } 
    
    @Override 
    public void draw(Graphics2D g) { 
        g.setColor(new Color(255, 200, 50, 75)); 
        g.fillOval((int)(x-vx*2), (int)(y-vy*2), 8, 8); 
        g.setColor(new Color(255, 230, 80, 120)); 
        g.fillOval((int)(x-vx), (int)(y-vy), 8, 8); 
        g.setColor(new Color(255, 240, 100)); 
        g.fillOval(x, y, w, h); 
        g.setColor(Color.WHITE); 
        g.fillOval(x+2, y+2, 5, 5); 
    } 
}

class Particle {
    float x, y, vx, vy; 
    int life, maxLife, size; 
    Color color; 
    
    public Particle(float x, float y, Color c, int life) { 
        this.x = x; 
        this.y = y; 
        this.color = c; 
        this.life = this.maxLife = life; 
        this.size = 3 + (int)(Math.random() * 5); 
        float angle = (float)(Math.random() * Math.PI * 2); 
        float spd = 1.5f + (float)(Math.random() * 3.5f); 
        vx = (float)Math.cos(angle) * spd; 
        vy = (float)Math.sin(angle) * spd; 
    } 
    
    public void update() { 
        x += vx; 
        y += vy; 
        vy += 0.12f; 
        vx *= 0.93f; 
        life--; 
    } 
    
    public boolean dead() { return life <= 0; } 
    
    public void draw(Graphics2D g) { 
        float a = (float) life / maxLife; 
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(a * 200))); 
        g.fillOval((int)x, (int)y, size, size); 
    } 
}

class Item {
    int x, y; 
    String type; 
    boolean collected = false; 
    float pulse = 0; 
    
    public Item(int x, int y, String type) { 
        this.x = x; 
        this.y = y; 
        this.type = type; 
    } 
    
    public Rectangle bounds() { return new Rectangle(x-12, y-12, 28, 28); } 
    public void update() { pulse += 0.12f; } 
    
    public void draw(Graphics2D g) { 
        float sc = 1f + (float)Math.sin(pulse) * 0.1f; 
        int sz = (int)(22 * sc); 
        int dx = x - sz/2, dy = y - sz/2; 
        if (type.equals("health")) { 
            g.setColor(new Color(0, 170, 65, 160)); 
            g.fillOval(dx-5, dy-5, sz+10, sz+10); 
            g.setColor(new Color(0, 255, 100)); 
            g.fillRect(dx+8, dy+2, 7, sz-4); 
            g.fillRect(dx+2, dy+8, sz-4, 7); 
            g.setColor(Color.WHITE); 
            g.fillRect(dx+9, dy+3, 5, sz-6); 
            g.fillRect(dx+3, dy+9, sz-6, 5); 
        } else { 
            g.setColor(new Color(170, 120, 0, 160)); 
            g.fillOval(dx-5, dy-5, sz+10, sz+10); 
            g.setColor(new Color(240, 200, 20)); 
            g.fillRoundRect(dx+1, dy+3, sz-2, sz-6, 5, 5); 
            g.setColor(new Color(100, 70, 0)); 
            g.setFont(new Font("Arial", Font.BOLD, 8)); 
            g.drawString("AMMO", dx+2, dy+sz-4); 
        } 
    } 
}

class FText {
    float x, y; 
    String txt; 
    Color col; 
    int life = 55; 
    
    public FText(float x, float y, String txt, Color col) { 
        this.x=x; this.y=y; this.txt=txt; this.col=col; 
    } 
    
    public void update() { 
        y -= 0.9f; 
        life--; 
    } 
    
    public boolean dead() { return life <= 0; } 
    
    public void draw(Graphics2D g) { 
        float a = Math.min(1f, life / 25f); 
        g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), (int)(a*225))); 
        g.setFont(new Font("Arial Black", Font.BOLD, 13)); 
        g.drawString(txt, (int)x, (int)y); 
    } 
}

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener, MouseListener { 
    static final int W = 900, H = 650; 
    private javax.swing.Timer timer; 
    private Player player; 
    private final List<Zombie> zombies = new ArrayList<>(); 
    private final List<Bullet> bullets = new ArrayList<>(); 
    private final List<Particle> parts = new ArrayList<>(); 
    private final List<Item> items = new ArrayList<>(); 
    private final List<FText> texts = new ArrayList<>(); 
    
    private int wave = 1; 
    private int normalKillsInCurrentLoop = 0;
    private boolean fastZombieSpawned = false;
    private boolean fastZombieDead = false;
    private boolean bossZombieSpawned = false;
    private boolean bossZombieDead = false;
    
    private int toSpawn = 0; 
    private int spawned = 0; 
    private int remaining = 0; 
    private long lastSpawn = 0; 
    private boolean clearing = false; 
    private final long clearStart = 0; 
    private int ammo = 40; 
    private final int maxAmmo = 40; 
    private boolean reloading = false; 
    private long reloadStart = 0; 
    private final int reloadMs = 1800; 
    private boolean over = false; 
    private boolean paused = false; 
    private int shakeFr = 0; 
    private float shakeMg = 0; 
    private final Random rng = new Random(); 
    private BufferedImage floor; 
    
    public GamePanel() { 
        setPreferredSize(new Dimension(W, H)); 
        setBackground(Color.BLACK); 
        setFocusable(true); 
        addKeyListener(this); 
        addMouseMotionListener(this); 
        addMouseListener(this); 
        startGame(); 
    } 
    
    private void startGame() { 
        player = new Player(W/2 - 18, H/2 - 18); 
        zombies.clear(); 
        bullets.clear(); 
        parts.clear(); 
        items.clear(); 
        texts.clear(); 
        wave = 1; 
        normalKillsInCurrentLoop = 0;
        fastZombieSpawned = false;
        fastZombieDead = false;
        bossZombieSpawned = false;
        bossZombieDead = false;
        ammo = maxAmmo; 
        reloading = false; 
        over = false; 
        paused = false; 
        buildFloor(); 
        startWave(); 
        if (timer != null) timer.stop(); 
        timer = new javax.swing.Timer(14, this); 
        timer.start(); 
    } 
    
    private void buildFloor() { 
        floor = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB); 
        Graphics2D g = floor.createGraphics(); 
        g.setColor(new Color(22, 25, 17)); 
        g.fillRect(0, 0, 64, 64); 
        g.setColor(new Color(30, 34, 22)); 
        g.drawRect(1, 1, 62, 62); 
        g.setColor(new Color(36, 40, 26)); 
        g.drawLine(0, 32, 64, 32); 
        g.drawLine(32, 0, 32, 64); 
        g.dispose(); 
    } 
    
    private void startWave() { 
        toSpawn = 999999; 
        spawned = 0; 
        remaining = 999999; 
        clearing = false; 
        lastSpawn = System.currentTimeMillis(); 
    } 
    
    @Override 
    public void actionPerformed(ActionEvent e) { 
        if (!over && !paused) update(); 
        repaint(); 
    } 
    
    private void update() { 
        player.update(W, H); 
        doAutoShoot(); 
        
        if (reloading && System.currentTimeMillis() - reloadStart >= reloadMs) { 
            ammo = maxAmmo; 
            reloading = false; 
            texts.add(new FText(player.x, player.y - 20, "RELOADED!", new Color(0, 220, 255))); 
        } 
        
        if (!clearing && zombies.size() < 6) { 
            long delay = 1200L; 
            if (System.currentTimeMillis() - lastSpawn >= delay) { 
                if (normalKillsInCurrentLoop < 10) {
                    spawnSpecificZombie(1);
                    lastSpawn = System.currentTimeMillis();
                } else if (!fastZombieSpawned) {
                    spawnSpecificZombie(2);
                    fastZombieSpawned = true;
                    lastSpawn = System.currentTimeMillis();
                } else if (fastZombieDead && !bossZombieSpawned) {
                    spawnSpecificZombie(3);
                    bossZombieSpawned = true;
                    lastSpawn = System.currentTimeMillis();
                }
            } 
        } 
        
        if (bossZombieDead) {
            wave++;
            normalKillsInCurrentLoop = 0;
            fastZombieSpawned = false;
            fastZombieDead = false;
            bossZombieSpawned = false;
            bossZombieDead = false;
            texts.add(new FText(W / 2 - 50, H / 2, "WAVE " + wave + " STARTED!", new Color(255, 50, 255)));
            
            if (rng.nextBoolean()) {
                items.add(new Item(rng.nextInt(W - 100) + 50, rng.nextInt(H - 140) + 70, "health"));
            } else {
                items.add(new Item(rng.nextInt(W - 100) + 50, rng.nextInt(H - 140) + 70, "ammo"));
            }
        }
        
        List<Zombie> dead = new ArrayList<>(); 
        for (int i = 0; i < zombies.size(); i++) { 
            Zombie z = zombies.get(i); 
            z.update(player.x + player.w/2, player.y + player.h/2); 
            for (int j = 0; j < zombies.size(); j++) { 
                if (i != j) z.separateFrom(zombies.get(j)); 
            } 
            if (z.canBite(player)) { 
                z.bite(player); 
                shake(5, 5); 
                addParticles(player.x+18, player.y+18, new Color(220,0,0), 8); 
                texts.add(new FText(player.x + rng.nextInt(20), player.y - 10 - rng.nextInt(10), "-" + z.dmg + " HP!", new Color(255, 40, 40))); 
            } 
            if (!z.isAlive()) dead.add(z); 
        } 
        
        for (Zombie z : dead) { 
            addParticles(z.x+z.w/2, z.y+z.h/2, new Color(155,18,18), 14); 
            player.addXP(z.getXP()); 
            player.addScore(z.getScore()); 
            player.addKill(); 
            remaining--; 
            texts.add(new FText(z.x, z.y-10, "+"+z.getScore(), new Color(255,215,50))); 
            
            if (z instanceof NormalZombie) {
                normalKillsInCurrentLoop++;
            } else if (z instanceof FastZombie) {
                fastZombieDead = true;
            } else if (z instanceof BossZombie) {
                bossZombieDead = true;
            }
            
            zombies.remove(z); 
        } 
        
        List<Bullet> usedB = new ArrayList<>(); 
        for (int i = 0; i < bullets.size(); i++) { 
            Bullet b = bullets.get(i); 
            b.update(); 
            if (!b.isAlive()) { 
                usedB.add(b); 
                continue; 
            } 
            for (int j = 0; j < zombies.size(); j++) { 
                Zombie z = zombies.get(j); 
                if (z.isAlive() && b.bounds().intersects(z.bounds())) { 
                    z.takeDamage(b.getDmg()); 
                    texts.add(new FText(b.x, b.y-8, "-"+b.getDmg(), new Color(255,70,70))); 
                    addParticles(b.x, b.y, new Color(195,20,20), 5); 
                    shake(2, 2); 
                    usedB.add(b); 
                    break; 
                } 
            } 
        } 
        bullets.removeAll(usedB); 
        
        List<Particle> dp = new ArrayList<>(); 
        for (Particle p : parts) { 
            p.update(); 
            if (p.dead()) dp.add(p); 
        } 
        parts.removeAll(dp); 
        
        List<FText> dt = new ArrayList<>(); 
        for (FText ft : texts) { 
            ft.update(); 
            if (ft.dead()) dt.add(ft); 
        } 
        texts.removeAll(dt); 
        
        List<Item> di = new ArrayList<>(); 
        for (Item item : items) { 
            item.update(); 
            if (!item.collected && item.bounds().intersects(player.bounds())) { 
                if (item.type.equals("health")) { 
                    int h2 = Math.min(40, player.maxHp - player.hp); 
                    player.hp += h2; 
                    texts.add(new FText(item.x, item.y-15, "+"+h2+" HP", new Color(0,230,80))); 
                } else { 
                    ammo = maxAmmo; 
                    reloading = false; 
                    texts.add(new FText(item.x, item.y-15, "AMMO FULL!", new Color(255,215,0))); 
                } 
                item.collected = true; 
            } 
            if (item.collected) di.add(item); 
        } 
        items.removeAll(di); 
        
        if (shakeFr > 0) shakeFr--; 
        if (!player.isAlive()) { 
            over = true; 
        } 
    } 
    
    private void doAutoShoot() { 
        if (reloading || !player.isAlive()) return; 
        if (!player.readyToShoot()) return; 
        if (ammo <= 0) { 
            startReload(); 
            return; 
        } 
        int bx = player.x + player.w/2; 
        int by = player.y + player.h/2; 
        int mx = player.getAimX(); 
        int my = player.getAimY(); 
        double dist = Math.sqrt((mx-bx)*(double)(mx-bx)+(my-by)*(double)(my-by)); 
        if (dist < 20) return; 
        bullets.add(new Bullet(bx, by, mx, my, player.getDmg())); 
        player.markShot(); 
        ammo--; 
        addParticles(bx, by, new Color(255,205,55), 4); 
        shake(1, 1); 
    } 
    
    private void startReload() { 
        if (!reloading && ammo < maxAmmo) { 
            reloading = true; 
            reloadStart = System.currentTimeMillis(); 
        } 
    } 
    
    private void spawnSpecificZombie(int choice) {
        int side = rng.nextInt(4); 
        int sx, sy; 
        int m = 30; 
        switch (side) { 
            case 0 -> {
                sx = rng.nextInt(W-60)+30; sy = m;
            }
            case 1 -> {
                sx = rng.nextInt(W-60)+30; sy = H-m; 
            }
            case 2 -> {
                sx = m; sy = rng.nextInt(H-100)+50;
            }
            default -> {
                sx = W-m; sy = rng.nextInt(H-100)+50;
            }
        }
        
        Zombie z;
        z = switch (choice) {
            case 2 -> new FastZombie(sx, sy);
            case 3 -> new BossZombie(sx, sy);
            default -> new NormalZombie(sx, sy);
        };
        
        zombies.add(z); 
        spawned++; 
    }
    
    private void spawnZombie() {} 
    
    private void addParticles(int x, int y, Color c, int n) { 
        for (int i = 0; i < n; i++) { 
            Color v = rng.nextBoolean() ? c : c.darker(); 
            parts.add(new Particle(x, y, v, 20 + rng.nextInt(25))); 
        } 
    } 
    
    private void shake(int fr, float mg) { 
        shakeFr = fr; 
        shakeMg = mg; 
    } 
    
    @Override 
    protected void paintComponent(Graphics g0) { 
        super.paintComponent(g0); 
        Graphics2D g = (Graphics2D) g0; 
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        int sx = shakeFr > 0 ? (int)(rng.nextFloat()*shakeMg*2-shakeMg) : 0; 
        int sy = shakeFr > 0 ? (int)(rng.nextFloat()*shakeMg*2-shakeMg) : 0; 
        if (sx!=0||sy!=0) g.translate(sx, sy); 
        drawFloor(g); 
        drawWalls(g); 
        for (Item it : items) it.draw(g); 
        for (Particle p : parts) p.draw(g); 
        for (Zombie z : zombies) z.draw(g); 
        if (player.isAlive()) player.draw(g); 
        for (Bullet b : bullets) b.draw(g); 
        for (FText ft : texts) ft.draw(g); 
        if (sx!=0||sy!=0) g.translate(-sx, -sy); 
        drawHUD(g); 
        drawCrosshair(g); 
        if (clearing) drawWaveBanner(g); 
        if (over) drawGameOver(g); 
        if (paused && !over) drawPaused(g); 
    } 
    
    private void drawFloor(Graphics2D g) { 
        if (floor == null) return; 
        for (int tx = 0; tx < W; tx += 64) 
            for (int ty = 0; ty < H; ty += 64) 
                g.drawImage(floor, tx, ty, null); 
    } 
    
    private void drawWalls(Graphics2D g) { 
        int t = 14; 
        g.setColor(new Color(65, 42, 12)); 
        g.fillRect(0, 0, W, t); 
        g.fillRect(0, H-t, W, t); 
        g.fillRect(0, 0, t, H); 
        g.fillRect(W-t, 0, t, H); 
        g.setColor(new Color(170, 120, 45, 90)); 
        g.setStroke(new BasicStroke(2)); 
        g.drawRect(t, t, W-t*2, H-t*2); 
        g.setStroke(new BasicStroke(1)); 
    } 
    
    private void drawHUD(Graphics2D g) { 
        g.setColor(new Color(0,0,0,165)); 
        g.fillRect(0, 0, W, 65); 
        g.setColor(new Color(255,255,255,12)); 
        g.drawLine(0, 64, W, 64); 
        g.setFont(new Font("Monospaced", Font.BOLD, 11)); 
        g.setColor(new Color(0, 225, 85)); 
        g.drawString("HP", 14, 17); 
        int hbw = 155, hbh = 14; 
        g.setColor(new Color(50,0,0)); 
        g.fillRoundRect(14, 20, hbw, hbh, 6, 6); 
        float hr = (float)player.hp / player.maxHp; 
        g.setColor(hr>.5f ? new Color(0,205,70) : hr>.25f? new Color(255,155,0) : Color.RED); 
        g.fillRoundRect(14, 20, (int)(hbw*hr), hbh, 6, 6); 
        g.setColor(new Color(255,255,255,65)); 
        g.drawRoundRect(14, 20, hbw, hbh, 6, 6); 
        g.setFont(new Font("Monospaced", Font.BOLD, 10)); 
        g.setColor(Color.WHITE); 
        g.drawString(player.hp+"/"+player.maxHp, 20, 31); 
        g.setColor(new Color(110,75,0,100)); 
        g.fillRoundRect(14, 37, hbw, 7, 3, 3); 
        g.setColor(new Color(255,185,0)); 
        g.fillRoundRect(14, 37, (int)(hbw*(float)player.getXP()/player.getXPMax()), 7, 3, 3); 
        g.setFont(new Font("Monospaced", Font.PLAIN, 10)); 
        g.setColor(new Color(255,225,140)); 
        g.drawString("LVL "+player.getLevel()+" XP:"+player.getXP()+"/"+player.getXPMax(), 14, 55); 
        String at = reloading ? "RELOADING..." : ammo+"/"+maxAmmo; 
        Color ac = reloading ? new Color(255,140,0) : ammo<6 ? Color.RED : new Color(255,210,40); 
        g.setFont(new Font("Monospaced", Font.BOLD, 14)); 
        g.setColor(ac); 
        FontMetrics fm = g.getFontMetrics(); 
        g.drawString(at, W/2-fm.stringWidth(at)/2, 20); 
        if (reloading) { 
            float rp = (float)(System.currentTimeMillis()-reloadStart)/reloadMs; 
            g.setColor(new Color(50,35,0)); 
            g.fillRoundRect(W/2-65, 24, 130, 7, 3, 3); 
            g.setColor(new Color(255,170,0)); 
            g.fillRoundRect(W/2-65, 24, (int)(130*rp), 7, 3, 3); 
        } 
        g.setFont(new Font("Monospaced", Font.PLAIN, 10)); 
        g.setColor(new Color(0,245,145,170)); 
        g.drawString("AUTO-SHOOT ON", W/2-45, 38); 
        g.setFont(new Font("Arial Black", Font.BOLD, 14)); 
        g.setColor(new Color(255,85,85)); 
        g.drawString("WAVE: " + wave, W-165, 19); 
        g.setFont(new Font("Monospaced", Font.PLAIN, 11)); 
        g.setColor(new Color(195,135,135)); 
        g.drawString("Alive: "+zombies.size(), W-165, 33); 
        g.setFont(new Font("Monospaced", Font.BOLD, 12)); 
        g.setColor(new Color(175,195,255)); 
        g.drawString("SCORE: "+player.getScore(), W-165, 48); 
        g.setColor(new Color(255,205,45)); 
        g.drawString("KILLS: "+player.getKills(), W-165, 61); 
        g.setFont(new Font("Monospaced", Font.PLAIN, 10)); 
        g.setColor(new Color(255,255,255,42)); 
        g.drawString("WASD: Move | Mouse: Aim & Auto-Shoot | R: Reload | P: Pause", 10, H-7); 
    } 
    
    private void drawCrosshair(Graphics2D g) { 
        int mx = player.getAimX(), my = player.getAimY(); 
        g.setColor(new Color(255,255,255,110)); 
        g.setStroke(new BasicStroke(1.5f)); 
        g.drawLine(mx-11, my, mx-4, my); 
        g.drawLine(mx+4, my, mx+11, my); 
        g.drawLine(mx, my-11, mx, my-4); 
        g.drawLine(mx, my+4, mx, my+11); 
        g.drawOval(mx-4, my-4, 8, 8); 
        g.setStroke(new BasicStroke(1)); 
    } 
    
    private void drawWaveBanner(Graphics2D g) {} 
    
    private void drawGameOver(Graphics2D g) { 
        g.setColor(new Color(0,0,0,185)); 
        g.fillRect(0,0,W,H); 
        g.setFont(new Font("Arial Black", Font.BOLD, 55)); 
        String t = "YOU DIED"; 
        FontMetrics fm = g.getFontMetrics(); 
        g.setColor(new Color(155,0,0,100)); 
        g.drawString(t, W/2-fm.stringWidth(t)/2+4, H/2-65+4); 
        g.setColor(new Color(215,22,22)); 
        g.drawString(t, W/2-fm.stringWidth(t)/2, H/2-65); 
        int bx = W/2-195, by = H/2-30; 
        g.setColor(new Color(14,14,14,205)); 
        g.fillRoundRect(bx,by,390,145,18,18); 
        g.setColor(new Color(95,22,22)); 
        g.setStroke(new BasicStroke(2)); 
        g.drawRoundRect(bx,by,390,145,18,18); 
        g.setStroke(new BasicStroke(1)); 
        g.setFont(new Font("Monospaced", Font.PLAIN, 17)); 
        g.setColor(new Color(225,225,225)); 
        String[] st={ "Score : "+player.getScore(), "Kills : "+player.getKills(), "Wave : "+wave, "Level : "+player.getLevel() }; 
        for (int i=0;i<st.length;i++) g.drawString(st[i], bx+55, by+35+i*28); 
        int btnX=W/2-115, btnY=H/2+130; 
        g.setColor(new Color(155,22,22)); 
        g.fillRoundRect(btnX,btnY,230,45,13,13); 
        g.setColor(new Color(255,85,85)); 
        g.setStroke(new BasicStroke(2)); 
        g.drawRoundRect(btnX,btnY,230,45,13,13); 
        g.setStroke(new BasicStroke(1)); 
        g.setFont(new Font("Arial Black", Font.BOLD, 17)); 
        g.setColor(Color.WHITE); 
        String btn="PLAY AGAIN [R]"; 
        g.drawString(btn, btnX+115-g.getFontMetrics().stringWidth(btn)/2, btnY+30); 
    } 
    
    private void drawPaused(Graphics2D g) { 
        g.setColor(new Color(0,0,0,150)); 
        g.fillRect(0,0,W,H); 
        g.setFont(new Font("Arial Black", Font.BOLD, 44)); 
        g.setColor(new Color(255,210,75)); 
        String p="PAUSED"; 
        FontMetrics fm=g.getFontMetrics(); 
        g.drawString(p, W/2-fm.stringWidth(p)/2, H/2); 
        g.setFont(new Font("Monospaced", Font.PLAIN, 16)); 
        g.setColor(Color.LIGHT_GRAY); 
        String s="Press P to resume"; 
        g.drawString(s, W/2-g.getFontMetrics().stringWidth(s)/2, H/2+38); 
    } 
    
    @Override 
    public void keyPressed(KeyEvent e) { 
        int k = e.getKeyCode(); 
        if (k==KeyEvent.VK_W||k==KeyEvent.VK_UP) player.goUp = true; 
        if (k==KeyEvent.VK_S||k==KeyEvent.VK_DOWN) player.goDown = true; 
        if (k==KeyEvent.VK_A||k==KeyEvent.VK_LEFT) player.goLeft = true; 
        if (k==KeyEvent.VK_D||k==KeyEvent.VK_RIGHT) player.goRight = true; 
        if (k==KeyEvent.VK_R) { 
            if (over) startGame(); 
            else startReload(); 
        } 
        if (k==KeyEvent.VK_P && !over) paused = !paused; 
        if (k==KeyEvent.VK_ESCAPE) System.exit(0); 
    } 
    
    @Override 
    public void keyReleased(KeyEvent e) { 
        int k = e.getKeyCode(); 
        if (k==KeyEvent.VK_W||k==KeyEvent.VK_UP) player.goUp = false; 
        if (k==KeyEvent.VK_S||k==KeyEvent.VK_DOWN) player.goDown = false; 
        if (k==KeyEvent.VK_A||k==KeyEvent.VK_LEFT) player.goLeft = false; 
        if (k==KeyEvent.VK_D||k==KeyEvent.VK_RIGHT) player.goRight = false; 
    } 
    
    @Override public void keyTyped(KeyEvent e) {} 
    @Override public void mouseMoved(MouseEvent e) { player.setAim(e.getX(), e.getY()); } 
    @Override public void mouseDragged(MouseEvent e) { player.setAim(e.getX(), e.getY()); } 
    @Override public void mousePressed(MouseEvent e) { if (over) startGame(); } 
    @Override public void mouseClicked(MouseEvent e) {} 
    @Override public void mouseReleased(MouseEvent e) {} 
    @Override public void mouseEntered(MouseEvent e) {} 
    @Override public void mouseExited(MouseEvent e) {} 
}
