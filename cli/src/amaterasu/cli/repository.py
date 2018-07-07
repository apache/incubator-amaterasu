"""
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""
from . import consts, common
import git
import os


class AmaRepository:

    def __init__(self, root_path):
        """

        :param root_path:
        :param user_info:
        :type user_info: common.User
        """
        self.root_path = root_path
        self.src_path = os.path.abspath('{}/src'.format(root_path))
        self.env_path = os.path.abspath('{}/env'.format(root_path))
        os.makedirs(self.root_path, exist_ok=True)
        self.git_repository = git.Repo.init(self.root_path)
        # self.signature = pygit2.Signature(user_info.name, user_info.email)

    @property
    def exists(self):
        return os.path.exists('{}/.git'.format(self.root_path))

    def init_repo(self):
        default_env = os.path.abspath('{}/default'.format(self.env_path))
        os.makedirs(self.src_path, exist_ok=True)
        os.makedirs(self.env_path, exist_ok=True)
        os.makedirs(default_env, exist_ok=True)
        if not os.path.exists('{}/{}'.format(self.root_path, consts.MAKI)):
            with open('{}/{}'.format(self.root_path, consts.MAKI), 'w') as f:
                f.write(common.RESOURCES[consts.MAKI])
        if not os.path.exists('{}/{}'.format(default_env, consts.JOB_FILE)):
            with open('{}/{}'.format(default_env, consts.JOB_FILE), 'w') as f:
                f.write(common.RESOURCES[consts.JOB_FILE])
        if not os.path.exists('{}/{}'.format(default_env, consts.SPARK_CONF)):
            with open('{}/{}'.format(default_env, consts.SPARK_CONF), 'w') as f:
                f.write(common.RESOURCES[consts.SPARK_CONF])

    def commit(self):
        self.git_repository.index.commit("Amaterasu job repo init")
