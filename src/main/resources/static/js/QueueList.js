class QueueList {
    constructor() {
        this.head = null;
        this.tail = null;
        this._size = 0;
    }

    enqueue(element) {
        const newNode = new Node(element);
        
        if (this._size == 0) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            this.tail.next = newNode
            this.tail = newNode;
        }

        this._size++;
    }

    dequeue() {
        if (this._size === 0) {
            throw new Error("this is empty")
        }
        var temp = this.head.value;
        this.head = this.head.next;
        this._size--;

        if(this._size === 0){
            this.tail = null;
        }

        return temp;
    }

    first(){
        if(this._size === 0){
            return null;
        }
        return this.head.value;
    }

    size() {
        return this._size;
    }
}

class Node {
    constructor(value = 0, next = null) {
        this.value = value;
        this.next = next;
    }
}

export default QueueList;