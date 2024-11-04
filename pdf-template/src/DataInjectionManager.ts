type InjectionHandler = (data: unknown) => void;

class DataInjectionManager {
  private listeners: Array<InjectionHandler> = [];

  addDataInjectionListener(listener: InjectionHandler) {
    this.listeners.push(listener);
  }

  removeDataInjectionListener(listener: InjectionHandler) {
    const index = this.listeners.indexOf(listener);
    if (index > -1) {
      this.listeners.splice(index, 1);
    }
  }

  inject(data: unknown) {
    this.listeners.forEach((listener) => listener(data));
  }
}

declare global {
  interface Window {
    injectData: (data: unknown) => void;
  }
}

const dataInjectionManager = new DataInjectionManager();
window.injectData = (data: unknown) => dataInjectionManager.inject(data);

export default dataInjectionManager;
