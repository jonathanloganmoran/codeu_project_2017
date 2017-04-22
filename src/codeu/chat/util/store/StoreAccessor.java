package codeu.chat.util.store;

// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
public interface StoreAccessor<KEY,VALUE> {


    VALUE first(KEY key);

    Iterable<VALUE> all();

    Iterable<VALUE> at(KEY key);

    Iterable<VALUE> after(KEY start);

    Iterable<VALUE> before(KEY end);

    Iterable<VALUE> range(KEY start, KEY end);

  }
