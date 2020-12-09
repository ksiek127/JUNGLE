public class Vector2D {
    public int x;
    public int y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int newX){
        x = newX;
    }

    public void setY(int newY){
        y = newY;
    }

    public Vector2D add(Vector2D other){ //suma wektorow
        return new Vector2D(this.x + other.x, this.y + other.y);
    }

    public boolean precedes(Vector2D other){ //czy ten wektor jest 'poprzednikiem' wektora podanego jako argument, czyli czy odpowiednie wspolrzedne sa mniejsze lub rowne wspolrzednym drugiego wektora
        return this.x <= other.x && this.y <= other.y;
    }

    public boolean follows(Vector2D other){ //analogicznie nastepnik
        return this.x >= other.x && this.y >= other.y;
    }

    public boolean equals(Vector2D other){
        return this.x == other.getX() && this.y == other.getY();
    }
}
