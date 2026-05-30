package com.kingspan.challenge.requests.stateMachine;

import com.kingspan.challenge.requests.PurchaseRequest;
import com.kingspan.challenge.requests.RequestStatus;
import com.kingspan.challenge.users.ApproverLevel;
import com.kingspan.challenge.users.User;
import com.kingspan.challenge.users.UserRole;
import org.springframework.stereotype.Service;

// 1 - PENDING → APPROVED — somente por APROVADOR com nível compatível
// 2 - PENDING → REJECTED — somente por APROVADOR com nível compatível
// 3 - PENDING → CANCELLED — pelo próprio SOLICITANTE ou por ADMIN
// 4 - APPROVED → qualquer coisa — BLOQUEADO (estado final)
// 5 - REJECTED → qualquer coisa — BLOQUEADO (estado final)
// 6 - CANCELLED → qualquer coisa — BLOQUEADO (estado final)

@Service
public class StateMachineService {


    // Validações de transição de estado para approved
    public void validateApproval(PurchaseRequest request, User actor){

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidStateTransitionException(
                    "Não é possível aprovar uma solicitação com status atual " +
                            request.getStatus().name().toLowerCase()+"."
            );
        }

        if (!canApprove(actor, request.getRequiredLevel())) {
            throw new InvalidStateTransitionException(
                    "Seu nível de aprovador não é suficiente para essa requisição");
        }
    }


    // Validações de transição de estado para rejected
    public void validateReject(PurchaseRequest request, User actor) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidStateTransitionException(
                    "Não é possível rejeitar uma solicitação com status atual " +
                            request.getStatus().name().toLowerCase()+"."
            );
        }

        if (!canApprove(actor, request.getRequiredLevel())) {
            throw new InvalidStateTransitionException(
                    "Seu nível de aprovação não é suficiente para rejeitar essa solicitação"
            );
        }
    }

    //Validações de transição de estada para cancelled
    public void validateCancel(PurchaseRequest request, User actor) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidStateTransitionException(
                    "Não é possível cancelar uma solicitação com status atual " +
                            request.getStatus().name().toLowerCase()+"."
            );
        }

        boolean isOwner = request.getRequestor().getId().equals(actor.getId());
        boolean isAdmin = actor.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new InvalidStateTransitionException(
                    "Apenas o solicitante ou administrador pode cancelar essa solicitação"
            );
        }


    }


    private boolean canApprove(User actor, ApproverLevel requiredLevel) {
        //Regra geral para ADMIN (aprova qualquer nível)
        if (actor.getRole() == UserRole.ADMIN) return true;

        //Regra para demais aprovadores, com base no nível de aprovação
        if (actor.getRole() == UserRole.APROVADOR) {
            return switch (requiredLevel) {
                case NIVEL_1 -> true; //Compras de até R$ 10.0000 qualquer aprovador aprova
                case NIVEL_2 -> actor.getApproverLevel() == ApproverLevel.NIVEL_2; // Retorna TRUE se o nível de aprovador for de nível 2 para comrpas nível 2
                case NIVEL_3 -> false; //Condição atendida no primeiro IF, onde ADMIN aprova todos os casos
            };
        }

        return false; //Role SOLICITANTE não aprova nenhuma requisição
    }





}
