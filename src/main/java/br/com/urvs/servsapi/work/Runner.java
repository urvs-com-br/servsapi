package br.com.urvs.servsapi.work;

import br.com.urvs.servsapi.data.Authed;
import br.com.urvs.servsapi.data.Group;
import br.com.urvs.servsapi.data.Way;
import br.com.urvs.servsapi.swap.Logged;
import br.com.urvs.servsapi.swap.TryAuth;
import jakarta.servlet.http.HttpServletRequest;

public class Runner {
  public static Way getWay(HttpServletRequest req) {
    return (Way) req.getServletContext().getAttribute("ServSapi.Way");
  }

  public static Logged tryEnter(TryAuth tryAuth, Way way, HttpServletRequest req) {
    for (var user : way.air.users) {
      if (user.name.equals(tryAuth.name) && user.pass.equals(tryAuth.pass)) {
        var token = req.getSession().getId();
        Group group = null;
        if (!user.group.isEmpty()) {
          for (var grouped : way.air.groups) {
            if (user.group.equals(grouped.name)) {
              group = grouped;
              break;
            }
          }
        }
        var authed = new Authed(user, group);
        way.authedMap.addAuthed(token, authed);
        return new Logged(token, authed.getLang());
      }
    }
    return null;
  }

  public static Authed getAuthed(Way way, HttpServletRequest req) {
    return way.authedMap.getAuthed(Runner.getToken(req));
  }

  public static String getToken(HttpServletRequest req) {
    return req.getSession().getId();
  }
}
