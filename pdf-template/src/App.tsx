import { useEffect, useState } from "react";
import dataInjectionManager from "./DataInjectionManager";
import { flushSync } from "react-dom";

type User = {
  name: string;
  email: string;
};

function App() {
  const [user, setUser] = useState<User>()

  useEffect(()=>{
    const onDataInjected = (data: unknown) => {
      flushSync(()=>{
        setUser(data as User)
      })
      console.log("DATA-INJECTED")
    }

    dataInjectionManager.addDataInjectionListener(onDataInjected)
    console.log("INJECTOR-PREPARED")

    return () => {
      dataInjectionManager.removeDataInjectionListener(onDataInjected)
    }
  }, [])

  return (
    <>
      <ul>
        <li>Name: {user?.name}</li>
        <li>Email: {user?.email}</li>
      </ul>
    </>
  );
}

export default App;
