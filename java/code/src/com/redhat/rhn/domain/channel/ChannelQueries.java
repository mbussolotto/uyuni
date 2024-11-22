package com.redhat.rhn.domain.channel;

class UyuniQuery{
    public String query;
    public String[] tables;

    public UyuniQuery(String queryIn, String[] tablesIn) {
        query = queryIn;
        tables = tablesIn;
    }
}

public interface ChannelQueries {

    String findRedHatBaseChannels = """
            from com.redhat.rhn.domain.channel.Channel as c where c.org is null
                                                            and parentChannel is null order by c.name
            """;

    String findCompatCustomBaseChsSSMNoBase = """
        SELECT c.*
        FROM
                (SELECT c.id, count(s.id) cnt
        FROM rhnServer s
        JOIN rhnSet rset ON rset.element = s.id AND rset.user_id = :user_id AND rset.label = 'system_list'
        LEFT JOIN rhnServerChannel sc ON sc.server_id = s.id
        JOIN rhnServerChannelArchCompat scac ON scac.server_arch_id = s.server_arch_id
        JOIN rhnChannel c ON c.channel_arch_id = scac.channel_arch_id
        WHERE c.parent_channel IS NULL
        AND sc.channel_id IS NULL
        AND (c.org_id = :org_id OR
                        (C.id, C.org_id)
        IN
                (SELECT scv.id, scv.org_id
                        FROM rhnSharedChannelView scv
                         WHERE scv.org_trust_id = :org_id
                         AND scv.parent_channel IS NULL))
        GROUP BY (c.id)) ch
        JOIN rhnChannel c ON c.id = ch.id
        LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
        WHERE ch.cnt =
                (SELECT COUNT(*)
        FROM rhnSet rset
        LEFT JOIN rhnServerChannel sc ON sc.server_id = rset.element
        WHERE rset.label = 'system_list'
        AND rset.user_id = :user_id
        AND sc.channel_id IS NULL)
        ORDER BY UPPER(c.name)
    """;

    String findCompatCustomBaseChsSSM = """
            SELECT c.*
                FROM
                    (SELECT c.id, count(s.id) cnt
                     FROM rhnServer s
                     JOIN rhnSet rset ON rset.element = s.id AND rset.user_id = :user_id AND rset.label = 'system_list'
                     JOIN rhnServerChannel sc ON sc.server_id = s.id AND sc.channel_id = :channel_id
                     JOIN rhnServerChannelArchCompat scac ON scac.server_arch_id = s.server_arch_id
                     JOIN rhnChannel c ON c.channel_arch_id = scac.channel_arch_id
                     WHERE c.parent_channel IS NULL
                     AND (c.org_id = :org_id OR
                         (C.id, C.org_id)
                         IN
                         (SELECT scv.id, scv.org_id
                          FROM rhnSharedChannelView scv
                          WHERE scv.ORG_TRUST_ID = :org_id
                            AND scv.parent_channel IS NULL))
                    GROUP BY (c.id)) ch
                JOIN rhnChannel c ON c.id = ch.id
                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                WHERE ch.cnt =
                    (SELECT COUNT(*)
                     FROM rhnServerChannel sc
                     JOIN rhnSet rset ON rset.element = sc.server_id
                     WHERE rset.label = 'system_list'
                        AND rset.user_id = :user_id
                        AND sc.channel_id = :channel_id)
                ORDER BY UPPER(c.name)
            """;

    String findCompatibleForChannelSSMInNullOrg = """
            SELECT c.*
                FROM
                    (SELECT dcm.channel_id, COUNT(s.id) cnt
                     FROM rhnServer s
                     JOIN rhnSet rset ON rset.element = s.id AND rset.user_id = :user_id AND rset.label = 'system_list'
                     JOIN rhnServerChannel sc ON sc.server_id = s.id
                     JOIN rhnServerChannelArchCompat scac ON scac.server_arch_id = s.server_arch_id
                     JOIN rhnDistChannelMap dcm ON dcm.channel_arch_id = scac.channel_arch_id AND dcm.release = s.release AND dcm.org_id IS NULL
                     WHERE sc.channel_id = :channel_id
                     GROUP BY dcm.channel_id) ch
                JOIN rhnChannel c ON c.id = ch.channel_id
                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                WHERE ch.cnt =
                    (SELECT COUNT(*)
                     FROM rhnServerChannel sc
                     JOIN rhnSet rset ON rset.element = sc.server_id
                     WHERE rset.label = 'system_list'
                        AND rset.user_id = :user_id
                        AND sc.channel_id = :channel_id)
            """;

    String findCompatibleSSMNoBaseInNullOrg = """
            SELECT c.*
                FROM
                    (SELECT dcm.channel_id, COUNT(s.id) cnt
                     FROM rhnServer s
                     JOIN rhnSet rset ON rset.element = s.id AND rset.user_id = :user_id AND rset.label = 'system_list'
                     LEFT JOIN rhnServerChannel sc ON sc.server_id = s.id
                     JOIN rhnServerChannelArchCompat scac ON scac.server_arch_id = s.server_arch_id
                     JOIN rhnDistChannelMap dcm ON dcm.channel_arch_id = scac.channel_arch_id AND dcm.release = s.release AND dcm.org_id IS NULL
                     WHERE sc.channel_id IS NULL
                     GROUP BY dcm.channel_id) ch
                JOIN rhnChannel c ON c.id = ch.channel_id
                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                WHERE ch.cnt =
                    (SELECT COUNT(*)
                     FROM rhnSet rset
                     LEFT JOIN rhnServerChannel sc ON sc.server_id = rset.element
                     WHERE rset.label = 'system_list'
                        AND rset.user_id = :user_id
                        AND sc.channel_id IS NULL)
            """;

    String findChildChannelsByParentInSSM = """
            select distinct c.id as channelId, c.name as channelName, c.org_id as channelOrg
                from
                                rhnChannelFamilyMembers cfm,
                                rhnChannelFamily cf,
                                rhnServerChannelArchCompat scac,
                                rhnChannel c,
                                rhnUserServerPerms usp,
                                rhnSet st,
                                rhnServer s
                where   st.user_id = :user_id
                        and st.label = 'system_list'
                        and st.element = s.id
                        and usp.user_id = :user_id
                        and st.element = usp.server_id
                        and s.server_arch_id = scac.server_arch_id
                        and scac.channel_arch_id = c.channel_arch_id
                        and c.id = cfm.channel_id
                        and cfm.channel_family_id = cf.id
                        and c.parent_channel = :parent_id
                        and cf.label not in ('rhn-satellite','rhn-proxy', 'SMS', 'SMS-X86', 'SMS-Z', 'SMS-PPC', 'SMP')
                        and (select deny_reason
                              from suseChannelUserRoleView scur
                              where scur.channel_id = c.id and
                                scur.user_id = :user_id and
                                scur.role = 'subscribe'
                        ) is null
            """;

    String findRedHatBaseChannelsByUserId = """
            SELECT c.*
                            FROM rhnChannel c
                                INNER JOIN suseChannelUserRoleView SCURV ON SCURV.channel_id = c.id
                                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                            WHERE c.org_id is null
                                AND c.parent_channel is null
                                AND SCURV.user_id = :userId
                                AND SCURV.deny_reason IS NULL
            """;

    String findCustomBaseChannels = """
            SELECT c.*
                            FROM suseChannelUserRoleView SCURV,
                                rhnChannel c left outer join rhnChannelCloned c_1_
                                ON c.id = c_1_.id
                            WHERE c.id = SCURV.channel_id
                                AND SCURV.user_id = :user_id
                                AND SCURV.deny_reason IS NULL
                                AND c.org_id is not null
                                AND SCURV.role = 'subscribe'
                                            AND c.parent_channel is null
                                order by C.name
            """;

    String findSubscribableBaseChannels = """
            SELECT c.*
             FROM suseChannelUserRoleView SCURV JOIN rhnChannel c ON c.id = SCURV.channel_id
                 LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
             WHERE SCURV.user_id = :user_id
                 AND SCURV.deny_reason IS NULL
                 AND SCURV.role = 'subscribe'
                 AND c.parent_channel is null
                 ORDER BY c.name
            """;

    String findAllBaseChannelsOnSatellite = """
            from com.redhat.rhn.domain.channel.Channel as c
                              where parentChannel is null
            """;

    String listAllChildren = """
            from com.redhat.rhn.domain.channel.Channel as c where c.parentChannel = :parent
            """;

    String findByLabelAndUserId = """
            SELECT c.* , c_1_.original_id,
                   CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
              FROM rhnChannel c
                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
             WHERE c.label = :label AND
               EXISTS (SELECT 1
                  FROM suseChannelUserRoleView scur
                  WHERE scur.channel_id = c.id AND
                    scur.user_id = :userId AND
                    deny_reason IS NULL
               )
            """;

    String findByLabelAndOrgId = """
            SELECT c.*, c_1_.original_id, CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
              FROM rhnChannel c
                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
             WHERE c.label = :label
               AND (rhn_channel.get_org_access(c.id, :orgId) = 1
                   OR EXISTS (select id from rhnSharedChannelView scv
                               where scv.label = :label
                                 and scv.org_trust_id = :orgId))
            """;

    String findChannelIdsByLabels = """
            SELECT c.id
              FROM com.redhat.rhn.domain.channel.Channel c
             WHERE c.label in (:labels)
            """;

    String findBaseChannel = """
            SELECT c.*
              FROM rhnServerChannel sc, rhnChannel c
                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
             WHERE sc.server_id = :sid
               AND sc.channel_id = c.id
               AND c.parent_channel IS NULL
            """;

    String channelsWithClonableErrata = """
            from com.redhat.rhn.domain.channel.ClonedChannel as c where c.org = :org
            """;

    String packageInChannelAndErrata = """
            select cp.package_id
              from rhnChannelPackage cp
              join rhnErrataPackage ep
                on ep.package_id = cp.package_id
             where cp.channel_id = :cid
               and ep.errata_id in (:eids)
            """;

    String accessibleChildChannelIds = """
            SELECT c.*
              FROM   rhnChannel c
                     LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
             WHERE   parent_channel = :cid
                 AND rhn_channel.get_org_access(c.id, :org_id) = 1
             UNION
             SELECT c.*
             FROM    rhnChannel c
             LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id,
                     rhnSharedChannelView sc
             WHERE   sc.parent_channel = :cid
             AND     sc.org_trust_id = :org_id
             AND     sc.id = c.id
            """;

    String packageByFileName = """
            SELECT p.*,
              (CASE WHEN C.id = :channel_id THEN 1 ELSE 0 end) as chanprio
              FROM rhnPackage p,
                 rhnChannelPackage CP,
                 rhnChannel C
                 WHERE (C.id = :channel_id OR C.parent_channel = :channel_id)
                   AND CP.channel_id = C.id
                   AND CP.package_id = P.id
                   AND P.path LIKE :pathlike
                   ORDER BY chanprio DESC, p.build_time
            """;

    String packageByFileNameAndRange = """
            SELECT p.*,
                    (CASE WHEN C.id = :channel_id THEN 1 ELSE 0 end) as chanprio
               FROM rhnPackage p,
                    rhnChannelPackage CP,
                    rhnChannel C
              WHERE (C.id = :channel_id OR C.parent_channel = :channel_id)
                AND CP.channel_id = C.id
                AND CP.package_id = P.id
                AND P.path LIKE :pathlike
                AND P.header_start = :headerStart
            AND P.header_end   = :headerEnd
           ORDER BY chanprio DESC, p.build_time
           """;

    String isAccessibleByUser = """
            SELECT 1
               FROM rhnChannel c
               JOIN suseChannelUserRoleView scur on c.id = scur.channel_id
              WHERE c.label = :channelLabel
                AND scur.user_id = :userId
                AND scur.channel_id = c.id
                AND deny_reason IS NULL
            """;

    String lookupOriginal = """
            select c.original
              from com.redhat.rhn.domain.channel.ClonedChannel as c where c = :clone
            """;

    String findChannelArchLabelsSyncdChannels = """
            select distinct c.channelArch.label from com.redhat.rhn.domain.channel.Channel as c
            """;

    String findCustomChannelsWithRepositories = """
            from com.redhat.rhn.domain.channel.Channel as c
                where c.org is not null and c.sources is not empty
            """;

    String findVendorChannels = """
            from com.redhat.rhn.domain.channel.Channel as c
             where c.org is null
            """;

    String findVendorRepositoryByChannelId = """
            SELECT DISTINCT r.*
                FROM rhnChannel c
                JOIN suseProductSCCRepository pr ON c.label = pr.channel_label
                JOIN suseSCCRepository r ON pr.repo_id = r.id
               WHERE c.org_id IS NULL
                AND c.id = :cid
            """;

    String findOrphanVendorChannels = """
            SELECT c.*
                FROM rhnChannel c
                LEFT JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                left join rhnChannelContentSource ccs on c.id = ccs.channel_id
                where c.org_id is NULL
                and ccs.source_id IS NULL
            """;

    String findModularChannels = """
            SELECT c.*
                FROM rhnChannel c
                LEFT JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                WHERE c.org_id = :org_id
                AND c.id in (
                  SELECT DISTINCT a.channel_id
                  FROM suseAppstream a)
    """;

    String verifyLabel = """
            select label from rhnChannel where label = :label
            """;

    String verifyName = """
            select name from rhnChannel where name = :name
            """;

    String getPackageCount = """
            select count(*) as package_count from rhnChannelPackage cp where cp.channel_id = :cid
            """;

    String getErrataCount = """
            select count(*) as errata_count from rhnChannelErrata cp where cp.channel_id = :cid
            """;

    String getPackageIdList = """
            select cp.package_id from rhnChannelPackage cp where cp.channel_id = :cid
            """;

    String getClonedErrataOriginalIdList = """
            SELECT errataCloned.original_id
            FROM rhnChannelErrata channelErrata
            INNER JOIN rhnErrataCloned errataCloned ON errataCloned.id = channelErrata.errata_id
            WHERE channelErrata.channel_id = :cid
            """;

    String isAccessibleBy = """
            SELECT case when (EXISTS (
                  SELECT 1
                  FROM rhnChannel c
                  JOIN rhnChannelFamilyMembers cfm ON cfm.channel_id = c.id
                  JOIN rhnPrivateChannelFamily pcf ON pcf.channel_family_id = cfm.channel_family_id
                  WHERE c.label = :channel_label
                  AND pcf.org_id = :org_id
                  LIMIT 1
            ) OR EXISTS (
                  SELECT 1
                  FROM rhnChannel c
                  JOIN rhnChannelFamilyMembers cfm ON cfm.channel_id = c.id
                  JOIN rhnPublicChannelFamily pcf ON pcf.channel_family_id = cfm.channel_family_id
                  WHERE c.label = :channel_label
                  LIMIT 1
            ) OR EXISTS (
                  SELECT 1
                  FROM rhnChannel c
                  JOIN rhnTrustedOrgs tr ON c.org_id = tr.org_id
                  WHERE c.channel_access = 'public'
                  AND c.label = :channel_label
                  AND tr.org_trust_id = :org_id
                  LIMIT 1
            ) OR EXISTS (
                  SELECT 1
                  FROM rhnChannel c
                  JOIN rhnChannelTrust tr ON c.id = tr.channel_id
                  WHERE c.channel_access = 'protected'
                  AND c.label = :channel_label
                  AND tr.org_trust_id = :org_id
                  LIMIT 1
            )) then 1 else 0 end AS result
            """;

    // Moved MC queries to see how it looks like
    String AccessibleChildChannels = """
                SELECT c.*, c_1_.original_id,
                       CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
                  FROM rhnChannel c
       LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                 WHERE parent_channel = :cid
                   AND (SELECT deny_reason
                          FROM suseChannelUserRoleView scur
                         WHERE scur.channel_id = c.id
                           AND scur.user_id = :userId
                           AND scur.role = 'subscribe'
                       ) IS NULL
       """;

    String AccessibleChannelsByOrg = """
                        SELECT  c.*, c_1_.original_id, CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
                          FROM  rhnChannel c
                      LEFT JOIN rhnChannelCloned c_1_ ON c.id = c_1_.original_id
                           JOIN rhnAvailableChannels cfp ON c.id = cfp.channel_id
                          WHERE cfp.org_id = :org_id
                      """;

    String lookupByLabel = """
        SELECT c.*, c_1_.original_id, CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
              FROM rhnChannel c
                LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
             WHERE c.label = :label
               AND (rhn_channel.get_org_access(c.id, :orgId) = 1
                   OR EXISTS (select id from rhnSharedChannelView scv
                               where scv.label = :label
                                 and scv.org_trust_id = :orgId))
        """;

    String KickstartableTreeChannels = """
                        SELECT c.*, c_1_.original_id,
                               CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
                        FROM rhnChannel c
                            LEFT JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                            JOIN rhnAvailableChannels ach ON ach.channel_id = c.id
                            JOIN rhnChannelArch ca ON ca.id = ach.channel_arch_id
                        WHERE ach.org_id = :org_id
                            AND ach.channel_depth = 1
                        ORDER BY rhn_channel.channel_priority(ach.parent_or_self_id),
                            UPPER(ach.channel_name)
                        """;

    String KickstartableChannels = """
            SELECT DISTINCT c.*, c_1_.original_id,
                CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_,
                rhn_channel.channel_priority(ach.parent_or_self_id),
                UPPER(ach.channel_name)
            FROM rhnChannel c
                LEFT JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                JOIN rhnAvailableChannels ach ON ach.channel_id = c.id
                JOIN rhnChannelArch ca ON ca.id = ach.channel_arch_id
                JOIN rhnKickstartableTree kt ON kt.channel_id = c.id
                JOIN rhnKSInstallType ksit ON ksit.id = kt.install_type
            WHERE ach.org_id = :org_id
                AND ach.channel_depth = 1
                AND (ksit.label LIKE 'rhel%' OR ksit.label LIKE 'fedora%')
            ORDER BY rhn_channel.channel_priority(ach.parent_or_self_id),
                UPPER(ach.channel_name)
                """;

    String findAllByUserOrderByChild = """
    with user_channel_roles as materialized(
        select * from suseChannelUserRoleView s
        where s.user_id = :userId
        and s.deny_reason is null
    )
    SELECT channel.*, channel_1_.original_id, CASE WHEN channel_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
    FROM rhnChannel channel
        LEFT OUTER JOIN rhnChannel parent ON channel.parent_channel = parent.id
        LEFT OUTER JOIN rhnChannelCloned channel_1_ ON channel.id = channel_1_.id
    WHERE EXISTS (
        SELECT 1
        FROM user_channel_roles scur
        WHERE scur.channel_id = channel.id
    )
    AND (
        channel.parent_channel IS NULL
        OR (
            channel.parent_channel IS NOT NULL
            AND EXISTS (
                SELECT 1
                FROM user_channel_roles scur
                WHERE scur.channel_id = channel.parent_channel
            )
        )
    )
    ORDER BY
        channel.org_id NULLS FIRST,
        COALESCE(parent.label, channel.label),
        channel.parent_channel NULLS FIRST,
        channel.label
                        """;

    String listAllBaseChannels = """
                SELECT c.*, c_1_.original_id,
                       CASE WHEN c_1_.original_id IS NULL THEN 0 ELSE 1 END as clazz_
                  FROM suseChannelUserRoleView SCURV
                  JOIN rhnChannel c ON c.id = SCURV.channel_id
                  LEFT OUTER JOIN rhnChannelCloned c_1_ ON c.id = c_1_.id
                 WHERE SCURV.org_id = :org_id
                   AND SCURV.deny_reason IS NULL
                   AND SCURV.user_id = :user_id
                   AND SCURV.role = 'subscribe'
                   AND c.parent_channel is null
                ORDER BY c.name
                """;

}
