/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;

/**
 * A prepare message implementation.
 */

class PrepareMessage extends PropagationMessage {

    public PrepareMessage(Participant participant, Result result) {
        super(participant, result);
    }

    /**
     * A prepare message.
     *
     * @return Boolean True if YES vote, False if NO vote, null if
     * readonly vote.
     */

    @Override
    protected Boolean send() throws PropagationException {
        Participant part = getParticipant();
        int ret = 0;
        Boolean result = null;
        try {
            // 执行 prepare
            ret = part.prepare();
            if (ret == Participant.READ_ONLY) {
                result = null;
            } else {
                result = Boolean.TRUE;
            }
        } catch (HeurHazardException heurh) {
            throw new PropagationException(heurh, false);
        } catch (RollbackException jtr) {
            // NO vote.
            result = Boolean.FALSE;
        } catch (Exception e) {
            // here, participant might be indoubt!
            HeurHazardException heurh = new HeurHazardException();
            throw new PropagationException(heurh, false);

        }
        return result;
    }

    @Override
    public String toString() {
        return ("PrepareMessage to " + getParticipant());
    }

}
