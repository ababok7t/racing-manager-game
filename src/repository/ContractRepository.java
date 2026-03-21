package repository;

import model.Contract;

public class ContractRepository extends Repository<Contract> {
    public Contract save(Contract contract) {
        return super.save(contract, contract.getId());
    }
}

