/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controlador;

import com.controlador.exceptions.IllegalOrphanException;
import com.controlador.exceptions.NonexistentEntityException;
import com.controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mundo.Grupo;
import mundo.EquipoJugador;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mundo.Equipo;
import mundo.Partido;

/**
 *
 * @author Laura
 */
public class EquipoJpaController implements Serializable {

    public EquipoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Equipo equipo) throws RollbackFailureException, Exception {
        if (equipo.getEquipoJugadorList() == null) {
            equipo.setEquipoJugadorList(new ArrayList<EquipoJugador>());
        }
        if (equipo.getPartidoList() == null) {
            equipo.setPartidoList(new ArrayList<Partido>());
        }
        if (equipo.getPartidoList1() == null) {
            equipo.setPartidoList1(new ArrayList<Partido>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Grupo idGrupo = equipo.getIdGrupo();
            if (idGrupo != null) {
                idGrupo = em.getReference(idGrupo.getClass(), idGrupo.getIdGrupo());
                equipo.setIdGrupo(idGrupo);
            }
            List<EquipoJugador> attachedEquipoJugadorList = new ArrayList<EquipoJugador>();
            for (EquipoJugador equipoJugadorListEquipoJugadorToAttach : equipo.getEquipoJugadorList()) {
                equipoJugadorListEquipoJugadorToAttach = em.getReference(equipoJugadorListEquipoJugadorToAttach.getClass(), equipoJugadorListEquipoJugadorToAttach.getIdCampeonatoEquipoJugador());
                attachedEquipoJugadorList.add(equipoJugadorListEquipoJugadorToAttach);
            }
            equipo.setEquipoJugadorList(attachedEquipoJugadorList);
            List<Partido> attachedPartidoList = new ArrayList<Partido>();
            for (Partido partidoListPartidoToAttach : equipo.getPartidoList()) {
                partidoListPartidoToAttach = em.getReference(partidoListPartidoToAttach.getClass(), partidoListPartidoToAttach.getIdPartido());
                attachedPartidoList.add(partidoListPartidoToAttach);
            }
            equipo.setPartidoList(attachedPartidoList);
            List<Partido> attachedPartidoList1 = new ArrayList<Partido>();
            for (Partido partidoList1PartidoToAttach : equipo.getPartidoList1()) {
                partidoList1PartidoToAttach = em.getReference(partidoList1PartidoToAttach.getClass(), partidoList1PartidoToAttach.getIdPartido());
                attachedPartidoList1.add(partidoList1PartidoToAttach);
            }
            equipo.setPartidoList1(attachedPartidoList1);
            em.persist(equipo);
            if (idGrupo != null) {
                idGrupo.getEquipoList().add(equipo);
                idGrupo = em.merge(idGrupo);
            }
            for (EquipoJugador equipoJugadorListEquipoJugador : equipo.getEquipoJugadorList()) {
                Equipo oldIdEquipoOfEquipoJugadorListEquipoJugador = equipoJugadorListEquipoJugador.getIdEquipo();
                equipoJugadorListEquipoJugador.setIdEquipo(equipo);
                equipoJugadorListEquipoJugador = em.merge(equipoJugadorListEquipoJugador);
                if (oldIdEquipoOfEquipoJugadorListEquipoJugador != null) {
                    oldIdEquipoOfEquipoJugadorListEquipoJugador.getEquipoJugadorList().remove(equipoJugadorListEquipoJugador);
                    oldIdEquipoOfEquipoJugadorListEquipoJugador = em.merge(oldIdEquipoOfEquipoJugadorListEquipoJugador);
                }
            }
            for (Partido partidoListPartido : equipo.getPartidoList()) {
                Equipo oldLocalOfPartidoListPartido = partidoListPartido.getLocal();
                partidoListPartido.setLocal(equipo);
                partidoListPartido = em.merge(partidoListPartido);
                if (oldLocalOfPartidoListPartido != null) {
                    oldLocalOfPartidoListPartido.getPartidoList().remove(partidoListPartido);
                    oldLocalOfPartidoListPartido = em.merge(oldLocalOfPartidoListPartido);
                }
            }
            for (Partido partidoList1Partido : equipo.getPartidoList1()) {
                Equipo oldVisitanteOfPartidoList1Partido = partidoList1Partido.getVisitante();
                partidoList1Partido.setVisitante(equipo);
                partidoList1Partido = em.merge(partidoList1Partido);
                if (oldVisitanteOfPartidoList1Partido != null) {
                    oldVisitanteOfPartidoList1Partido.getPartidoList1().remove(partidoList1Partido);
                    oldVisitanteOfPartidoList1Partido = em.merge(oldVisitanteOfPartidoList1Partido);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Equipo equipo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Equipo persistentEquipo = em.find(Equipo.class, equipo.getIdEquipo());
            Grupo idGrupoOld = persistentEquipo.getIdGrupo();
            Grupo idGrupoNew = equipo.getIdGrupo();
            List<EquipoJugador> equipoJugadorListOld = persistentEquipo.getEquipoJugadorList();
            List<EquipoJugador> equipoJugadorListNew = equipo.getEquipoJugadorList();
            List<Partido> partidoListOld = persistentEquipo.getPartidoList();
            List<Partido> partidoListNew = equipo.getPartidoList();
            List<Partido> partidoList1Old = persistentEquipo.getPartidoList1();
            List<Partido> partidoList1New = equipo.getPartidoList1();
            List<String> illegalOrphanMessages = null;
            for (EquipoJugador equipoJugadorListOldEquipoJugador : equipoJugadorListOld) {
                if (!equipoJugadorListNew.contains(equipoJugadorListOldEquipoJugador)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain EquipoJugador " + equipoJugadorListOldEquipoJugador + " since its idEquipo field is not nullable.");
                }
            }
            for (Partido partidoListOldPartido : partidoListOld) {
                if (!partidoListNew.contains(partidoListOldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidoListOldPartido + " since its local field is not nullable.");
                }
            }
            for (Partido partidoList1OldPartido : partidoList1Old) {
                if (!partidoList1New.contains(partidoList1OldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidoList1OldPartido + " since its visitante field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idGrupoNew != null) {
                idGrupoNew = em.getReference(idGrupoNew.getClass(), idGrupoNew.getIdGrupo());
                equipo.setIdGrupo(idGrupoNew);
            }
            List<EquipoJugador> attachedEquipoJugadorListNew = new ArrayList<EquipoJugador>();
            for (EquipoJugador equipoJugadorListNewEquipoJugadorToAttach : equipoJugadorListNew) {
                equipoJugadorListNewEquipoJugadorToAttach = em.getReference(equipoJugadorListNewEquipoJugadorToAttach.getClass(), equipoJugadorListNewEquipoJugadorToAttach.getIdCampeonatoEquipoJugador());
                attachedEquipoJugadorListNew.add(equipoJugadorListNewEquipoJugadorToAttach);
            }
            equipoJugadorListNew = attachedEquipoJugadorListNew;
            equipo.setEquipoJugadorList(equipoJugadorListNew);
            List<Partido> attachedPartidoListNew = new ArrayList<Partido>();
            for (Partido partidoListNewPartidoToAttach : partidoListNew) {
                partidoListNewPartidoToAttach = em.getReference(partidoListNewPartidoToAttach.getClass(), partidoListNewPartidoToAttach.getIdPartido());
                attachedPartidoListNew.add(partidoListNewPartidoToAttach);
            }
            partidoListNew = attachedPartidoListNew;
            equipo.setPartidoList(partidoListNew);
            List<Partido> attachedPartidoList1New = new ArrayList<Partido>();
            for (Partido partidoList1NewPartidoToAttach : partidoList1New) {
                partidoList1NewPartidoToAttach = em.getReference(partidoList1NewPartidoToAttach.getClass(), partidoList1NewPartidoToAttach.getIdPartido());
                attachedPartidoList1New.add(partidoList1NewPartidoToAttach);
            }
            partidoList1New = attachedPartidoList1New;
            equipo.setPartidoList1(partidoList1New);
            equipo = em.merge(equipo);
            if (idGrupoOld != null && !idGrupoOld.equals(idGrupoNew)) {
                idGrupoOld.getEquipoList().remove(equipo);
                idGrupoOld = em.merge(idGrupoOld);
            }
            if (idGrupoNew != null && !idGrupoNew.equals(idGrupoOld)) {
                idGrupoNew.getEquipoList().add(equipo);
                idGrupoNew = em.merge(idGrupoNew);
            }
            for (EquipoJugador equipoJugadorListNewEquipoJugador : equipoJugadorListNew) {
                if (!equipoJugadorListOld.contains(equipoJugadorListNewEquipoJugador)) {
                    Equipo oldIdEquipoOfEquipoJugadorListNewEquipoJugador = equipoJugadorListNewEquipoJugador.getIdEquipo();
                    equipoJugadorListNewEquipoJugador.setIdEquipo(equipo);
                    equipoJugadorListNewEquipoJugador = em.merge(equipoJugadorListNewEquipoJugador);
                    if (oldIdEquipoOfEquipoJugadorListNewEquipoJugador != null && !oldIdEquipoOfEquipoJugadorListNewEquipoJugador.equals(equipo)) {
                        oldIdEquipoOfEquipoJugadorListNewEquipoJugador.getEquipoJugadorList().remove(equipoJugadorListNewEquipoJugador);
                        oldIdEquipoOfEquipoJugadorListNewEquipoJugador = em.merge(oldIdEquipoOfEquipoJugadorListNewEquipoJugador);
                    }
                }
            }
            for (Partido partidoListNewPartido : partidoListNew) {
                if (!partidoListOld.contains(partidoListNewPartido)) {
                    Equipo oldLocalOfPartidoListNewPartido = partidoListNewPartido.getLocal();
                    partidoListNewPartido.setLocal(equipo);
                    partidoListNewPartido = em.merge(partidoListNewPartido);
                    if (oldLocalOfPartidoListNewPartido != null && !oldLocalOfPartidoListNewPartido.equals(equipo)) {
                        oldLocalOfPartidoListNewPartido.getPartidoList().remove(partidoListNewPartido);
                        oldLocalOfPartidoListNewPartido = em.merge(oldLocalOfPartidoListNewPartido);
                    }
                }
            }
            for (Partido partidoList1NewPartido : partidoList1New) {
                if (!partidoList1Old.contains(partidoList1NewPartido)) {
                    Equipo oldVisitanteOfPartidoList1NewPartido = partidoList1NewPartido.getVisitante();
                    partidoList1NewPartido.setVisitante(equipo);
                    partidoList1NewPartido = em.merge(partidoList1NewPartido);
                    if (oldVisitanteOfPartidoList1NewPartido != null && !oldVisitanteOfPartidoList1NewPartido.equals(equipo)) {
                        oldVisitanteOfPartidoList1NewPartido.getPartidoList1().remove(partidoList1NewPartido);
                        oldVisitanteOfPartidoList1NewPartido = em.merge(oldVisitanteOfPartidoList1NewPartido);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = equipo.getIdEquipo();
                if (findEquipo(id) == null) {
                    throw new NonexistentEntityException("The equipo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Equipo equipo;
            try {
                equipo = em.getReference(Equipo.class, id);
                equipo.getIdEquipo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The equipo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<EquipoJugador> equipoJugadorListOrphanCheck = equipo.getEquipoJugadorList();
            for (EquipoJugador equipoJugadorListOrphanCheckEquipoJugador : equipoJugadorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Equipo (" + equipo + ") cannot be destroyed since the EquipoJugador " + equipoJugadorListOrphanCheckEquipoJugador + " in its equipoJugadorList field has a non-nullable idEquipo field.");
            }
            List<Partido> partidoListOrphanCheck = equipo.getPartidoList();
            for (Partido partidoListOrphanCheckPartido : partidoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Equipo (" + equipo + ") cannot be destroyed since the Partido " + partidoListOrphanCheckPartido + " in its partidoList field has a non-nullable local field.");
            }
            List<Partido> partidoList1OrphanCheck = equipo.getPartidoList1();
            for (Partido partidoList1OrphanCheckPartido : partidoList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Equipo (" + equipo + ") cannot be destroyed since the Partido " + partidoList1OrphanCheckPartido + " in its partidoList1 field has a non-nullable visitante field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Grupo idGrupo = equipo.getIdGrupo();
            if (idGrupo != null) {
                idGrupo.getEquipoList().remove(equipo);
                idGrupo = em.merge(idGrupo);
            }
            em.remove(equipo);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Equipo> findEquipoEntities() {
        return findEquipoEntities(true, -1, -1);
    }

    public List<Equipo> findEquipoEntities(int maxResults, int firstResult) {
        return findEquipoEntities(false, maxResults, firstResult);
    }

    private List<Equipo> findEquipoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Equipo.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Equipo findEquipo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Equipo.class, id);
        } finally {
            em.close();
        }
    }

    public int getEquipoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Equipo> rt = cq.from(Equipo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
