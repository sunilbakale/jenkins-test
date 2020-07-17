
<div id="page-content-wrapper">

    <nav class="navbar navbar-expand border-bottom top-navbar">
        <button class="btn btn-outline-primary" id="menu-toggle">
            <i class="fa fa-bars" aria-hidden="true"></i>
        </button>

        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav ml-auto mt-2 mt-lg-0">
              <#--<li class="nav-item active">
                <a class="nav-link" href="<@ofbizUrl>dashboard</@ofbizUrl>">Home <span class="sr-only">(current)</span></a>
              </li>-->

              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button"
                   data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                    <img width="22" height="22" class="round mr-2" avatar="${Static["org.apache.ofbiz.party.party.PartyHelper"].getPartyName(loggedInParty)}"/>
                    <span class="userName">${Static["org.apache.ofbiz.party.party.PartyHelper"].getPartyName(loggedInParty)}</span>

                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
                  <a class="dropdown-item" href="<@ofbizUrl>my_account</@ofbizUrl>">My Account</a>
                  <div class="dropdown-divider"></div>
                  <a class="dropdown-item" href="<@ofbizUrl>logout</@ofbizUrl>">Logout</a>
                </div>
              </li>
            </ul>
        </div>
    </nav>