package org.example.memory;

import org.example.exception.MemoryAccessException;
import org.example.mmu.BaseAddressRegister;
import org.example.mmu.LimitRegister;

public class AddressTranslator {
    public int getRealAddressKb(int addressKb, BaseAddressRegister baseAddressRegister, LimitRegister limitRegister) throws MemoryAccessException {
        if (addressKb < 0 || addressKb >= limitRegister.getLimitKb()) {
            throw new MemoryAccessException("Invalid address Kb" + addressKb);
        }
        return addressKb + baseAddressRegister.getBaseAddressKb();
    }
}

