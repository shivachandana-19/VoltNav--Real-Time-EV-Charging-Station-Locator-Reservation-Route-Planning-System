import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.subscriptions = new Map();
  }

  connect(onConnected) {
    if (this.client && this.connected) {
      if (onConnected) onConnected();
      return;
    }

    const socketFactory = () => new SockJS('/ws');

    this.client = new Client({
      webSocketFactory: socketFactory,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        this.connected = true;
        console.log('[VoltNav WS] Connected to WebSocket');
        if (onConnected) onConnected();
      },
      onDisconnect: () => {
        this.connected = false;
        console.log('[VoltNav WS] Disconnected');
      },
      onStompError: (frame) => {
        console.error('[VoltNav WS] Broker error: ' + frame.headers['message']);
      }
    });

    this.client.activate();
  }

  subscribeToSlots(onSlotUpdate) {
    const topic = '/topic/slots';

    const performSubscribe = () => {
      if (!this.client || !this.connected) return;

      const sub = this.client.subscribe(topic, (message) => {
        try {
          const payload = JSON.parse(message.body);
          if (onSlotUpdate) onSlotUpdate(payload);
        } catch (e) {
          console.error('[VoltNav WS] Error parsing slot update message', e);
        }
      });

      return sub;
    };

    if (this.connected) {
      const sub = performSubscribe();
      return () => {
        if (sub) sub.unsubscribe();
      };
    } else {
      let subInstance = null;
      this.connect(() => {
        subInstance = performSubscribe();
      });
      return () => {
        if (subInstance) subInstance.unsubscribe();
      };
    }
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.connected = false;
      this.client = null;
    }
  }
}

const wsService = new WebSocketService();
export default wsService;
