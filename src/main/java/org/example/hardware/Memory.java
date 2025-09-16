package org.example.hardware;

import org.example.hardware.exception.MemoryAccessException;
public class Memory {
    public static class AddressTranslator {
        public int getRealAddressKb(int addressKb) throws MemoryAccessException {

            if (addressKb < 0 || addressKb >= CPU.Register.LIMIT.get()) {
                throw new MemoryAccessException("Invalid address Kb" + addressKb);
            }
            return addressKb + CPU.Register.BASE_ADDRESS.get();
        }
    }


}
