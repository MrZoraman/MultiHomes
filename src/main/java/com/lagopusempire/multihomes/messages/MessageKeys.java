package com.lagopusempire.multihomes.messages;

/**
 *
 * @author MrZoraman
 */
public enum MessageKeys
{
    RELOAD_SUCCESS                  ("reload.success"),
    RELOAD_FAILURE                  ("reload.failure"),
    NO_PERMISSION                   ("noPermissions"),
    LOGIN_AND_STILL_LOADING         ("loginAndStillLoading"),
    MUST_BE_PLAYER                  ("mustBePlayer"),
    NOT_LOADED                      ("notLoaded"),
    
    HOME_SET_IMPLICIT               ("homeSet.implicit"),
    HOME_SET_EXPLICIT               ("homeSet.explicit"),
    HOME_SET_TOO_MANY               ("homeSet.tooMany"),
    
    HOME_GET_NOEXIST_IMPLICIT       ("homeGet.doesNotExist.implicit"),
    HOME_GET_NOEXIST_EXPLICIT       ("homeGet.doesNotExist.explicit"),
    HOME_GET_NOT_LOADED_IMPLICIT    ("homeGet.worldNotLoaded.implicit"),
    HOME_GET_NOT_LOADED_EXPLICIT    ("homeGet.worldNotLoaded.explicit"),
    
    HOME_LIST_INITIAL               ("homeList.initial"),
    HOME_LIST_FORMAT                ("homeList.format"),
    HOME_LIST_NONE                  ("homeList.none"),
    HOME_LIST_END_STRIP_LENGTH      ("homeList.endStripLength"),
    
    INFINITE_HOMES_REP              ("infiniteHomesRep"),
    
    HOME_DELETE_NOEXIST_IMPLICIT    ("homeDelete.noExist.implicit"),
    HOME_DELETE_NOEXIST_EXPLICIT    ("homeDelete.noExist.explicit"),
    HOME_DELETE_SUCCESS_IMPLICIT    ("homeDelete.success.implicit"),
    HOME_DELETE_SUCCESS_EXPLICIT    ("homeDelete.success.explicit");

    private final String key;

    private MessageKeys(String key)
    {
        this.key = key;
    }

    String getKey()
    {
        return key;
    }
}
